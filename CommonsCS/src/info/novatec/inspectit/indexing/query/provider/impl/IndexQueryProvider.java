// package info.novatec.inspectit.indexing.query.provider.impl;
//
// import info.novatec.inspectit.indexing.IIndexQuery;
// import info.novatec.inspectit.indexing.impl.IndexQuery;
// import info.novatec.inspectit.indexing.query.provider.IIndexQueryProvider;
//
// import org.springframework.beans.factory.ObjectFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
//
/// **
// * Class that is used for providing the correct instance of {@link IIndexQuery} via Spring
// * framework.
// *
// * @author Ivan Senic
// */
// @Component
// public class IndexQueryProvider implements IIndexQueryProvider<IIndexQuery> {
//
// /**
// *
// * @return Returns the correctly instated instance of {@link IIndexQuery} that can be used in
// * for querying the indexing tree.
// */
// // public abstract IndexQuery createNewIndexQuery();
//
// @Autowired
// ObjectFactory<IndexQuery> indexQueryFactory;
//
// /**
// * {@inheritDoc}
// */
// @Override
// public IndexQuery getIndexQuery() {
// return indexQueryFactory.getObject();
// }
// }
