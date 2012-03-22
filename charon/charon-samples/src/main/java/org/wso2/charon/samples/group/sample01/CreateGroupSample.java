/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.charon.samples.group.sample01;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.ClientHandler;
import org.wso2.charon.core.client.SCIMClient;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.samples.utils.CharonResponseHandler;
import org.wso2.charon.samples.utils.SampleConstants;
import org.wso2.charon.utils.authentication.BasicAuthHandler;
import org.wso2.charon.utils.authentication.BasicAuthInfo;

public class CreateGroupSample {
    //user details
    public static final String DISPLAY_NAME = "eng";
    public static final String EXTERNAL_ID = "eng";
    //replace following IDs with already existing member IDs.
    //hasinitg,hasini
    public static final String[] MEMBERS = {"9c9afc67-2897-4c8c-9461-5f7c7d724307",
                                            "b1eaefb8-2a56-4479-8a4c-2c7a4ad6ab4c"};

    public static void main(String[] args) {

        try {
            //create SCIM client
            SCIMClient scimClient = new SCIMClient();
            //create a group according to SCIM Group Schema
            Group scimGroup = scimClient.createGroup();
            scimGroup.setExternalId(EXTERNAL_ID);
            scimGroup.setDisplayName(DISPLAY_NAME);
            //set group members
            for (String member : MEMBERS) {
                scimGroup.setMember(member);
            }
            //encode the group in JSON format
            String encodedGroup = scimClient.encodeSCIMObject(scimGroup, SCIMConstants.JSON);
            //create a apache wink ClientHandler to intercept and identify response messages
            CharonResponseHandler responseHandler = new CharonResponseHandler();
            responseHandler.setSCIMClient(scimClient);
            //set the handler in wink client config
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.handlers(new ClientHandler[]{responseHandler});
            //create a wink rest client with the above config
            RestClient restClient = new RestClient(clientConfig);
            //create resource endpoint to access User resource
            Resource groupResource = restClient.resource(SampleConstants.GROUP_ENDPOINT);

            BasicAuthInfo basicAuthInfo = new BasicAuthInfo();
            basicAuthInfo.setUserName(SampleConstants.CRED_USER_NAME);
            basicAuthInfo.setPassword(SampleConstants.CRED_PASSWORD);

            BasicAuthHandler basicAuthHandler = new BasicAuthHandler();
            BasicAuthInfo encodedBasicAuthInfo = (BasicAuthInfo) basicAuthHandler.getAuthenticationToken(basicAuthInfo);

            //TODO:enable, disable SSL. For the demo purpose, we make the calls over http
            //send previously registered SCIM consumer credentials in http headers.
            String response = groupResource.
                    header(SCIMConstants.AUTHORIZATION_HEADER, encodedBasicAuthInfo.getAuthorizationHeader()).
                    contentType(SCIMConstants.APPLICATION_JSON).accept(SCIMConstants.APPLICATION_JSON).
                    post(String.class, encodedGroup);

            //decode the response
            System.out.println(response);

        } catch (CharonException e) {
            e.printStackTrace();
        } catch (ClientWebException e) {
            System.out.println(e.getRequest().getEntity());
            System.out.println(e.getResponse().getMessage());
            e.printStackTrace();
        }
    }
}
