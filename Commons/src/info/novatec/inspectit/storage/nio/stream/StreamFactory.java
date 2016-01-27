/**
 *
 */
package info.novatec.inspectit.storage.nio.stream;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory that is used for providing the correct instance of {@link ExtendedByteBufferOutputStream}
 * via Spring framework.
 *
 * @author Ivan Senic, Christoph Heger
 *
 */
@Component
public class StreamFactory {

	/**
	 * {@link ObjectFactory} to create new {@link ExtendedByteBufferOutputStream} instances.
	 */
	@Autowired
	private ObjectFactory<ExtendedByteBufferOutputStream> extendedByteBufferOutputStreamFactory;

	/**
	 * {@link ObjectFactory} to create new {@link SocketExtendedByteBufferOutputStream} instances.
	 */
	@Autowired
	private ObjectFactory<SocketExtendedByteBufferInputStream> socketExtendedByteBufferInputStreamFactory;

	/**
	 * @return Returns the newly initialized instance of the {@link ExtendedByteBufferOutputStream}
	 *         that has been prepared for use.
	 * @throws IOException
	 *             if output stream can not be obtained
	 */
	public ExtendedByteBufferOutputStream getExtendedByteBufferOutputStream() throws IOException {
		ExtendedByteBufferOutputStream stream = extendedByteBufferOutputStreamFactory.getObject();
		stream.prepare();
		return stream;
	}

	/**
	 * Returns the {@link SocketExtendedByteBufferInputStream} initialized by Spring and prepared.
	 * Caller must first call {@link SocketExtendedByteBufferInputStream#reset(int)} before reading
	 * any data.
	 *
	 * @param socketChannel
	 *            Underlying {@link SocketChannel} for the stream.
	 * @return {@link SocketExtendedByteBufferInputStream}.
	 * @throws IOException
	 *             if input stream can not be obtained
	 */
	public SocketExtendedByteBufferInputStream getSocketExtendedByteBufferInputStream(SocketChannel socketChannel) throws IOException {
		SocketExtendedByteBufferInputStream inputStream = socketExtendedByteBufferInputStreamFactory.getObject();
		inputStream.setSocketChannel(socketChannel);
		inputStream.prepare();
		return inputStream;
	}

}
