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
package org.wso2.charon.samples.group.sample03;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.ClientHandler;
import org.wso2.charon.core.client.SCIMClient;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.samples.utils.CharonResponseHandler;
import org.wso2.charon.samples.utils.SampleConstants;

public class DeleteGroupSample {
    public static final String GROUP_ID = "d6b59ddd-1133-4d9a-a4f8-72661ca39ede";

    public static void main(String[] args) {

        try {
            ClientConfig clientConfig = new ClientConfig();
            CharonResponseHandler responseHandler = new CharonResponseHandler();
            responseHandler.setSCIMClient(new SCIMClient());
            clientConfig.handlers(new ClientHandler[]{responseHandler});
            RestClient restClient = new RestClient(clientConfig);

            //create resource endpoint
            Resource groupResource = restClient.resource(SampleConstants.GROUP_ENDPOINT + GROUP_ID);

            //enable, disable SSL.
            //had to set content type for the delete request as well, coz wink client sets */* by default.
            String response = groupResource.
                    header(SCIMConstants.AUTH_HEADER_USERNAME, SampleConstants.CRED_USER_NAME).
                    header(SCIMConstants.AUTH_HEADER_PASSWORD, SampleConstants.CRED_PASSWORD).
                    accept(SCIMConstants.APPLICATION_JSON).
                    delete(String.class);

            //decode the response
            System.out.println(response);
        } catch (ClientWebException e) {
            System.out.println(e.getRequest().getEntity());
            System.out.println(e.getResponse().getMessage());
            e.printStackTrace();
        }

    }
}