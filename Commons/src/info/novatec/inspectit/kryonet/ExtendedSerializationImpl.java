package info.novatec.inspectit.kryonet;

import info.novatec.inspectit.storage.serializer.IKryoProvider;
import info.novatec.inspectit.storage.serializer.impl.SerializationManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.ObjectFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Implementation of the {@link IExtendedSerialization} with some additional methods we need for
 * (de-)serializing the object during the communication. The idea is not to (de-)serialize from/to
 * buffer, but to use the streams which would give us opportunity to transfer objects of unlimited
 * size.
 *
 * @author Ivan Senic
 *
 */
@SuppressWarnings("all")
public class ExtendedSerializationImpl implements IExtendedSerialization {

	/**
	 * Default initial number of created serializers.
	 */
	private static final int INIT_CREATED_SERIALIZERS = 2;

	/**
	 * Queue for {@link IKryoProvider} that are available.
	 */
	private final Queue<IKryoProvider> serializerQueue = new ConcurrentLinkedQueue<IKryoProvider>();

	/**
	 * {@link ObjectFactory} to create new {@link SerializationManager} instances.
	 */
	private final ObjectFactory<SerializationManager> serializationManagerFactory;

	/**
	 * One argument constructor. Same as calling
	 * {@link #ExtendedSerializationImpl(ObjectFactory, int)} with init serializers value of
	 * {@value #INIT_CREATED_SERIALIZERS}.
	 *
	 * @param serializationManagerFactory
	 *            {@link ObjectFactory} needed for creation of the {@link IKryoProvider} instances.
	 */
	public ExtendedSerializationImpl(ObjectFactory<SerializationManager> serializationManagerFactory) {
		this(serializationManagerFactory, INIT_CREATED_SERIALIZERS);
	}

	/**
	 * Default constructor.
	 *
	 * @param serializationManagerFactory
	 *            {@link ObjectFactory} needed for creation of the {@link IKryoProvider} instances.
	 * @param initialSerializersCreated
	 *            Amount of {@link IKryoProvider}s to be created immediately.
	 */
	public ExtendedSerializationImpl(ObjectFactory<SerializationManager> serializationManagerFactory, int initialSerializersCreated) {
		this.serializationManagerFactory = serializationManagerFactory;
		for (int i = 0; i < initialSerializersCreated; i++) {
			serializerQueue.offer(serializationManagerFactory.getObject());
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Not implemented. It's not used because we won't write to the buffer, but to the output
	 * stream. But it's defined in the Serialization interface, so we must implement it.
	 */
	public void write(Connection connection, ByteBuffer buffer, Object object) {
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Not implemented. It's not used because we won't read from the buffer, but from the input
	 * stream. But it's defined in the Serialization interface, so we must implement it.
	 */
	public Object read(Connection connection, ByteBuffer buffer) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void writeLength(ByteBuffer buffer, int length) {
		buffer.putInt(length);
	}

	/**
	 * {@inheritDoc}
	 */
	public int readLength(ByteBuffer buffer) {
		return buffer.getInt();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns 4 as the original Kryo serialization. This should represent number of bytes needed
	 * for storing the length of the bytes to send.
	 */
	public int getLengthLength() {
		return 4;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void write(Connection connection, OutputStream outputStream, Object object) {
		Output output = new Output(outputStream);
		IKryoProvider kryoProvider = serializerQueue.poll();

		// if nothing is available in queue don't wait, create new one
		if (null == kryoProvider) {
			kryoProvider = serializationManagerFactory.getObject();
		}

		try {
			Kryo kryo = kryoProvider.getKryo();
			kryo.getContext().put("connection", connection);
			kryo.writeClassAndObject(output, object);
			output.flush();
		} finally {
			serializerQueue.offer(kryoProvider);
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object read(Connection connection, InputStream inputStream) {
		Input input = new Input(inputStream);
		IKryoProvider kryoProvider = serializerQueue.poll();

		// if nothing is available in queue don't wait, create new one
		if (null == kryoProvider) {
			kryoProvider = serializationManagerFactory.getObject();
		}

		try {
			Kryo kryo = kryoProvider.getKryo();
			kryo.getContext().put("connection", connection);
			return kryo.readClassAndObject(input);
		} finally {
			serializerQueue.offer(kryoProvider);
		}
	}

}
