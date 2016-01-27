/**
 *
 */
package info.novatec.inspectit.storage.serializer.impl;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Christoph Heger
 *
 */
public class SerializationManagerFactory {

	@Autowired
	private ObjectFactory<SerializationManager> serializationManagerFactory;

	public SerializationManager getSerializationManager() {
		SerializationManager serializationManager = serializationManagerFactory.getObject();
		return serializationManager;
	}
}
