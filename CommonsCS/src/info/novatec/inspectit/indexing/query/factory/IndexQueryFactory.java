/**
 *
 */
package info.novatec.inspectit.indexing.query.factory;

import info.novatec.inspectit.indexing.impl.IndexQuery;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author Christoph Heger
 *
 */
public class IndexQueryFactory implements FactoryBean<IndexQuery> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndexQuery getObject() throws Exception {
		IndexQuery q = new IndexQuery();
		return q;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<IndexQuery> getObjectType() {
		// TODO Auto-generated method stub
		return IndexQuery.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}

}
