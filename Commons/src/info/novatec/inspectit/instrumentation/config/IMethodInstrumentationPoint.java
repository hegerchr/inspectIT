package info.novatec.inspectit.instrumentation.config;

import info.novatec.inspectit.org.objectweb.asm.MethodVisitor;

/**
 * Our single instrumentation point for a method.
 *
 * @author Ivan Senic
 *
 */
public interface IMethodInstrumentationPoint {

	/**
	 * Returns the {@link MethodVisitor} that will instrument the given method. Note that
	 * superMethodVistor must be included in the returned {@link MethodVisitor}, thus fulfilling the
	 * visitor pattern ASM is using.
	 *
	 * @param superMethodVisitor
	 *            the method visitor to which created adapter delegates calls
	 * @param access
	 *            the method's access flags
	 * @param name
	 *            the method's name
	 * @param desc
	 *            the method's descriptor
	 * @return Created {@link MethodVisitor}.
	 */
	MethodVisitor getMethodVisitor(MethodVisitor superMethodVisitor, int access, String name, String desc);

}