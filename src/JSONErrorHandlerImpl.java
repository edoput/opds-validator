package com.feedbooks.opds;

import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.text.MessageFormat;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import com.thaiopensource.util.UriOrFile;

import net.sf.json.*;

public class JSONErrorHandlerImpl extends ErrorHandlerImpl {
	private final String bundleName
		= "com.thaiopensource.xml.sax.resources.Messages";

	private ResourceBundle bundle = null;
	private List<JSONObject> errors;

	public JSONErrorHandlerImpl() {
		this(System.err);
	}

	public JSONErrorHandlerImpl(OutputStream os) {
		super(os);
		errors = new ArrayList<JSONObject>();
	}

	public JSONErrorHandlerImpl(Writer w) {
		super(w);
		errors=new ArrayList<JSONObject>();
	}

	public void close() {
                JSONArray serialized_errors = JSONArray.fromObject(errors);
                System.out.println(serialized_errors);
		//super.print(serialized_errors);
	}


	public void warning(SAXParseException e) throws SAXParseException {
		printSAXParseException("warning",e);
	}

	public void error(SAXParseException e) {
		printSAXParseException("error",e);
	}

	public void fatalError(SAXParseException e) throws SAXParseException {
		throw e;
	}

	public void printException(Throwable e) {
		String message;
		if (e instanceof SAXException)
			printSAXParseException("fatal",(SAXParseException)e);
		else
			System.err.println("fatal :"+formatMessage(e));
	}

	public void print(String message) {
		if (message.length() != 0) {
                        System.out.println(message);
			//errors.add(message);
		}
	}

	private String getString(String key) {
		if (bundle == null)
			bundle = ResourceBundle.getBundle(bundleName);
		return bundle.getString(key);
	}

	private String format(String key, Object[] args) {
		return MessageFormat.format(getString(key), args);
	}

	private void printSAXParseException(String severity,SAXParseException e){
                JSONObject ret = new JSONObject().element("severity",severity);

                String systemId = e.getSystemId();
                if (systemId!=null){
                        ret.put("location", UriOrFile.uriToUriOrFile(systemId));
                }
                int n = e.getLineNumber();
                if (n>=0){
                        ret.put("line",new Integer(n));
                }
                n = e.getColumnNumber();
                if (n>=0){
                        ret.put("column",new Integer(n));
                }
                ret.put("message",formatMessage(e));
                errors.add(ret);
	}
	private String formatLocation(SAXParseException e) {
		String systemId = e.getSystemId();
		int n = e.getLineNumber();
		Integer lineNumber = n >= 0 ? new Integer(n) : null;
		n = e.getColumnNumber();
		Integer columnNumber = n >= 0 ? new Integer(n) : null;
		if (systemId != null) {
			systemId = UriOrFile.uriToUriOrFile(systemId);
			if (lineNumber != null) {
				if (columnNumber != null)
					return format("locator_system_id_line_number_column_number",
							new Object[] { systemId, lineNumber, columnNumber });
				else
					return format("locator_system_id_line_number",
							new Object[] { systemId, lineNumber });
			}
			else
				return format("locator_system_id",
						new Object[] { systemId });
		}
		else if (lineNumber != null) {
			if (columnNumber != null)
				return format("locator_line_number_column_number",
						new Object[] { lineNumber, columnNumber });
			else
				return format("locator_line_number",
						new Object[] { lineNumber });
		}
		else
			return "";
	}

	private String formatMessage(SAXException se) {
		Exception e = se.getException();
		String detail = se.getMessage();
		if (e != null) {
			String detail2 = e.getMessage();
			// Crimson stupidity
			if (detail2 == detail || e.getClass().getName().equals(detail))
				return formatMessage(e);
			else if (detail2 == null)
				return format("exception",
						new Object[]{ e.getClass().getName(), detail });
			else
				return format("tunnel_exception",
						new Object[] { e.getClass().getName(),
							detail,
				       detail2 });
		}
		else {
			if (detail == null)
				detail = getString("no_detail");
			return detail;
		}
	}

	private String formatMessage(Throwable e) {
		String detail = e.getMessage();
		if (detail == null)
			detail = getString("no_detail");
		if (e instanceof FileNotFoundException)
			return format("file_not_found", new Object[] { detail });
		return format("exception",
				new Object[] { e.getClass().getName(), detail });
	}
}
