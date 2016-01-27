package info.novatec.inspectit.cmr.spring.exporter;

import info.novatec.inspectit.storage.serializer.ISerializer;
import info.novatec.inspectit.storage.serializer.SerializationException;
import info.novatec.inspectit.storage.serializer.impl.SerializationManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * This service exporter using kryo for (de-)serialization is nearly the same as the one with plain
 * java serialization.
 *
 * @author Patrice Bouillet, Christoph Heger
 *
 */
public class KryoHttpInvokerServiceExporter extends HttpInvokerServiceExporter {

	/**
	 * The serialization manager.
	 */
	@Autowired
	private ObjectFactory<SerializationManager> serializationManagerFactory;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RemoteInvocation readRemoteInvocation(HttpServletRequest request, InputStream is) throws IOException, ClassNotFoundException {
		try (Input input = new Input(is)) {
			ISerializer serializer = serializationManagerFactory.getObject();
			return (RemoteInvocation) serializer.deserialize(input);
		} catch (SerializationException e) {
			throw new IOException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeRemoteInvocationResult(HttpServletRequest request, HttpServletResponse response, RemoteInvocationResult result, OutputStream os) throws IOException {
		try (Output output = new Output(os)) {
			if (!result.hasException()) {
				Object value = result.getValue();
				result = new RemoteInvocationResult(value);
			}

			ISerializer serializer = serializationManagerFactory.getObject();
			serializer.serialize(result, output);
		} catch (SerializationException e) {
			throw new IOException(e);
		}
	}

}
