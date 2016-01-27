package info.novatec.inspectit.cmr.rmi;

import info.novatec.inspectit.kryonet.Connection;
import info.novatec.inspectit.kryonet.ExtendedSerializationImpl;
import info.novatec.inspectit.kryonet.IExtendedSerialization;
import info.novatec.inspectit.kryonet.Listener;
import info.novatec.inspectit.kryonet.Server;
import info.novatec.inspectit.kryonet.rmi.ObjectSpace;
import info.novatec.inspectit.spring.logger.Log;
import info.novatec.inspectit.storage.nio.stream.StreamFactory;
import info.novatec.inspectit.storage.serializer.impl.SerializationManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * COnfiguration of the {@link Server} that will be used for communication with the agent.
 *
 * @author Ivan Senic
 *
 */
@Configuration
public class KryoNetServerCreator {

	/**
	 * Logger for the class.
	 */
	@Log
	Logger log;

	/**
	 * Port to bind service to.
	 */
	@Value("${cmr.port}")
	private int port;

	/**
	 * Executor service for object space. This will enable that multiple incoming communication
	 * requests can be handled in parallel.
	 */
	@Autowired
	@Qualifier("kryoNetObjectSpaceExecutorService")
	private ExecutorService executorService;

	/**
	 * {@link ObjectFactory} to create {@link SerializationManager} instances.
	 */
	@Autowired
	private ObjectFactory<SerializationManager> serializationManagerFactory;

	/**
	 * Factory to create input and output streams to read and write from the socket connection.
	 */
	@Autowired
	private StreamFactory streamFactory;

	/**
	 * Start the kryonet server and binds it to the specified port.
	 *
	 * @return Start the kryonet server and binds it to the specified port.
	 */
	@Bean(name = "kryonet-server", destroyMethod = "stop")
	public Server createServer() {
		IExtendedSerialization serialization = new ExtendedSerializationImpl(serializationManagerFactory);

		Server server = new Server(serialization, streamFactory);
		server.start();

		try {
			server.bind(port);
			log.info("|-Kryonet server successfully started and running on port " + port);
		} catch (IOException e) {
			throw new BeanInitializationException("Could not bind the kryonet server to the specified ports.", e);
		}

		return server;
	}

	/**
	 * Creates the {@link ObjectSpace}, registers kryo classes and connect the space to the server.
	 *
	 * @param server
	 *            KryoNet {@link Server}.
	 * @return Created {@link ObjectSpace}.
	 */
	@Bean(name = "kryonet-server-objectspace")
	@DependsOn("kryonet-server")
	@Autowired
	public ObjectSpace createObjectSpace(Server server) {
		final ObjectSpace objectSpace = new ObjectSpace();
		objectSpace.setExecutor(executorService);
		server.addListener(new Listener() {
			@Override
			public void connected(Connection connection) {
				objectSpace.addConnection(connection);
			}
		});

		return objectSpace;
	}

}
