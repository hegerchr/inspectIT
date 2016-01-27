/**
 *
 */
package info.novatec.inspectit.storage.nio.stream;

import info.novatec.inspectit.indexing.storage.IStorageDescriptor;
import info.novatec.inspectit.storage.IStorageData;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory to create instances of {@link ExtendedByteBufferInputStream}.
 *
 * @author Ivan Senic, Christoph Heger
 *
 */
@Component
public class InputStreamFactory {

	/**
	 * {@link ObjectFactory} to create {@link ExtendedByteBufferInputStream} instances.
	 */
	@Autowired
	private ObjectFactory<ExtendedByteBufferInputStream> streamFactory;

	/**
	 * {@inheritDoc}
	 */
	public ExtendedByteBufferInputStream getExtendedByteBufferInputStream(IStorageData storageData, List<IStorageDescriptor> descriptors) throws IOException {
		if (storageData == null) {
			throw new IllegalStateException("Storage data not set.");
		}

		if (descriptors == null) {
			throw new IllegalStateException("Storage descriptors not set.");
		}

		ExtendedByteBufferInputStream stream = streamFactory.getObject();
		stream.setStorageData(storageData);
		stream.setDescriptors(descriptors);
		stream.prepare();
		return stream;
	}
}
