/*-----------------------------------------------------------------------------
 Class   :  AlmaUserInfo.java
 Author  :  Keith Folsom
 Date    :  11/22/13
 Purpose :  This class accesses Alma's Web Services interface to retrieve
            user information

 Credits :  This class is based on the example file USerWSExample.java that
            is included in the Ex Libris Alma SDK kit.

 License :  
 
   Copyright 2013 Orbis Cascade Alliance

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License

 Changes :
-----------------------------------------------------------------------------*/

package org.orbiscascade.code.sils.AlmaUserInfo;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

import com.exlibris.alma.sdk.AlmaWebServices;

public class AlmaUserInfo {
    private String mUsername;
    private String mWebServicesURL;
    private String mWebServicesAPIUserUsername;
    private String mWebServicesAPIUserPassword;
    private String mInstitutionCode;
    private String mAuthTestXPath;
    private boolean mShowAll;
    private boolean mCheckAuth;

    public AlmaUserInfo(boolean showAll, boolean checkAuth, String configFile, String username) {
        mShowAll = showAll;
        mCheckAuth = checkAuth;
        mUsername = username;
        
        try {
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(configFile);

		    XPathFactory xpathFactory = XPathFactory.newInstance();
		    XPath xpath = xpathFactory.newXPath();

		    XPathExpression expr = xpath.compile("/almaUserInfoConfig/webServicesURL/text()");
		    Object resultObject = expr.evaluate(doc, XPathConstants.NODESET);
		    NodeList nodes = (NodeList) resultObject;
            mWebServicesURL = nodes.item(0).getNodeValue().trim();

		    expr = xpath.compile("/almaUserInfoConfig/webServicesAPIUser/username/text()");
		    resultObject = expr.evaluate(doc, XPathConstants.NODESET);
		    nodes = (NodeList) resultObject;
            mWebServicesAPIUserUsername = nodes.item(0).getNodeValue().trim();

		    expr = xpath.compile("/almaUserInfoConfig/webServicesAPIUser/password/text()");
		    resultObject = expr.evaluate(doc, XPathConstants.NODESET);
		    nodes = (NodeList) resultObject;
            mWebServicesAPIUserPassword = nodes.item(0).getNodeValue().trim();
            
		    expr = xpath.compile("/almaUserInfoConfig/institutionCode/text()");
		    resultObject = expr.evaluate(doc, XPathConstants.NODESET);
		    nodes = (NodeList) resultObject;
            mInstitutionCode = nodes.item(0).getNodeValue().trim();

		    expr = xpath.compile("/almaUserInfoConfig/authTestXPath/text()");
		    resultObject = expr.evaluate(doc, XPathConstants.NODESET);
		    nodes = (NodeList) resultObject;
            mAuthTestXPath = nodes.item(0).getNodeValue().trim();

        } // try
        catch (Exception e) {
            e.printStackTrace();
        } // catch

    } // constructor AlmaUserInfo

    public void retrieve() {
        AlmaWebServices aws = AlmaWebServices.create(mWebServicesURL, mWebServicesAPIUserUsername, mWebServicesAPIUserPassword, mInstitutionCode);

        getUserInfo(aws, mUsername, null);
    }

    private void getUserInfo(AlmaWebServices aws, String userIdentifier, String identifierType) {

        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put(AlmaWebServices.INPUT_PARAM_1, userIdentifier);
        if (identifierType != null) {
            paramsMap.put(AlmaWebServices.INPUT_PARAM_2, identifierType);
        } else {
            // there is no identifierType
            paramsMap.put(AlmaWebServices.INPUT_PARAM_2, "");
        }

        invokeWS(aws, AlmaWebServices.GET_USER_DETAILS, paramsMap);

    }

    private void invokeWS(AlmaWebServices aws, String wsMethod, Map<String, String> params) {
        try {
            String xmlString = aws.invoke(AlmaWebServices.GET_USER_DETAILS_WS_LINK, wsMethod, params);
            if (mShowAll) {
                System.out.println(xmlString);
            } // if
            else if (mCheckAuth) {
                parseAndCheckAuth(xmlString);
            } // else if
            else {
                System.out.println(xmlString);
            } // else
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void parseAndCheckAuth(String xmlString) {
        try {
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    factory.setNamespaceAware(true); // never forget this!
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document doc = builder.parse(new InputSource(new ByteArrayInputStream(xmlString.getBytes("utf-8"))));

		    XPathFactory xpathFactory = XPathFactory.newInstance();
		    XPath xpath = xpathFactory.newXPath();

            xpath.setNamespaceContext(new NamespaceContext() {
                @Override
                public Iterator<Object> getPrefixes(String arg0) {
                    return null;
                }

                @Override
                public String getPrefix(String arg0) {
                    return null;
                }

                @Override
                public String getNamespaceURI(String arg0) {
                    if("xb".equals(arg0)) {
                        return "http://com/exlibris/urm/user_record/xmlbeans";
                    }
                    return null;
                }
            });

                /*
                 * The criteria for considering a user account as authorized are defined by
                 * an XPath expression retrieved from the configuration file.  This way,
                 * the criteria can be changed for an institution's particular set-up and
                 * user groups.
                 */
		    XPathExpression expr = xpath.compile(mAuthTestXPath);
            if (expr != null) {
		        Boolean isAuth = (Boolean) expr.evaluate(doc, XPathConstants.BOOLEAN);

                if (isAuth) {
			        System.out.print(1);
                } // if 2
                else if (!isAuth){
			        System.out.print(0);
                } // else if 2
            } // if
            else {
                System.out.print(-1);
            } // else

/*
		    XPathExpression expr = xpath.compile("//xb:userGroup/text()");
		    Object resultObject = expr.evaluate(doc, XPathConstants.NODESET);
		    NodeList nodes = (NodeList) resultObject;

		    for (int i = 0; i < nodes.getLength(); i++) {
			    System.out.println(nodes.item(i).getNodeValue());
		    } // for

//            System.out.println(result);
*/
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

} // class AlmaUserInfo
