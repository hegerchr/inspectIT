/// **
// *
// */
// package info.novatec.inspectit.rcp.ci.view.matchingrules;
//
// import info.novatec.inspectit.cmr.configuration.business.IExpression;
//
// import java.util.Map;
// import java.util.concurrent.atomic.AtomicInteger;
//
/// **
// * @author Alexander Wert
// *
// */
// public class Parser {
//
// "1 AND (2 OR 3 OR 4)"
// private static final String OPEN_BRACE = "[";
// private static final String OR = "OR";
// private static final String AND = "AND";
// private static final String NOT = "NOT";
// private static final String CLOSE_BRACE = "]";
// private static final String HTTP_URI_LEAF = "HTTP_URI";
// private final AtomicInteger publicIndex = new AtomicInteger(0);
//
// IExpression parse(String strExpression) {
//
// return null;
// }
//
// private String parseAnd(String strExpression){
// int startIndex = strExpression.indexOf(AND);
// if(startIndex > 0){
// strExpression.charAt(startIndex-1);
// if(){
//
// }
// }
// }
//
// private String findAndReplaceLeafs(String strExpression, Map<Integer, IExpression> expressionMap)
/// {
// int startIndex = strExpression.toUpperCase().indexOf(HTTP_URI_LEAF);
// int stopIndex = strExpression.indexOf(CLOSE_BRACE, startIndex) + 1;
// int expressionIndex = publicIndex.getAndIncrement();
// String httpUriExpressionStr = strExpression.substring(startIndex, stopIndex);
// IExpression httpUriExpression = null; // convert expression string
// expressionMap.put(expressionIndex, httpUriExpression);
// strExpression = strExpression.substring(0, startIndex) + String.valueOf(expressionIndex) +
/// strExpression.substring(stopIndex, strExpression.length());
// return strExpression.replaceAll("\\s", "");
// }
// }
