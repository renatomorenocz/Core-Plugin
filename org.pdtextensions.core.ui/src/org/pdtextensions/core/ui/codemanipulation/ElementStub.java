package org.pdtextensions.core.ui.codemanipulation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.core.INamespace;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.php.internal.core.typeinference.PHPModelUtils;
import org.pdtextensions.core.log.Logger;

public abstract class ElementStub {

	protected String code = null;
	protected IScriptProject scriptProject = null;
	protected String lineDelim = "\n";
	protected String name;
	protected String namespace;
	protected IType superclass;
	protected boolean isFinal;
	protected List<IType> interfaces;
	protected boolean generateComments;

	protected String generateInterfacesCode() {

		String code = new String();
		if (!interfaces.isEmpty()) {
			code = " implements";

			int size = interfaces.size();
			int i = 1;
			String prefix;
			for (IType interfaceObject : interfaces) {
				// If there are namespace we will add it in use section if not,
				// we need add "\" before name;
				prefix = (getUseNamespaceString(interfaceObject) == null ? "\\" : "");
				if (i < size) {
					code += " " + prefix + interfaceObject.getElementName() + ",";
				} else {
					code += " " + prefix + interfaceObject.getElementName();
				}
				i = i + 1;
			}
		}

		return code;
	}

	protected String generateNamespacePart() {
		String code = new String();
		if (this.namespace != null && this.namespace.length() > 0) {
			code = "namespace " + this.namespace + ";" + lineDelim + lineDelim;
		}

		for (String useNamespace : getUseNamespacesList()) {
			code += useNamespace + lineDelim;
		}
		code += lineDelim;

		return code;
	}

	private ArrayList<String> getUseNamespacesList() {
		// TODO: Create list of namespaces to avoid duplication;
		ArrayList<String> namespaces = new ArrayList<String>();
		if (superclass != null && getUseNamespaceString(superclass) != null) {
			namespaces.add(getUseNamespaceString(superclass));
		}
		if (interfaces != null) {
			for (IType interfaceType : interfaces) {
				String useString = getUseNamespaceString(interfaceType);
				if (useString != null)
					namespaces.add(useString);
			}
		}
		return namespaces;

	}

	protected String getUseNamespaceString(IType type) {
		String namespaceString = extractNamespaceName(type);
		if (namespaceString != null) {
			return "use " + namespaceString + "\\" + type.getElementName() + ";";
		}
		return null;
	}

	protected String extractNamespaceName(IType type) {
		return PHPModelUtils.extractNameSapceName(type.getFullyQualifiedName().replace("$", "\\"));
	}

	/**
	 * @return If created element is in namespace.
	 */
	private boolean isInNamespace() {
		return (namespace == null ? false : true);
	}

	protected abstract void generateCode();

	public String toString() {

		if (code == null) {
			generateCode();
		}

		return code;
	}

}