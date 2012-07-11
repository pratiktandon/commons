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
package org.wso2.siddhi.core.event.remove;

import org.wso2.siddhi.core.event.StateEvent;
import org.wso2.siddhi.core.event.StreamEvent;

/**
 * Event with state
 */
public class RemoveStateEvent extends StateEvent implements RemoveStream {

    long expiryTime = 0L;

    public RemoveStateEvent(StreamEvent[] newEventStream, long expiryTime) {
        super(newEventStream);
        this.expiryTime = expiryTime;
    }

    private RemoveStateEvent(int eventState, StreamEvent[] newEventStream,
                             long expiryTime) {
        super(eventState, newEventStream);
        this.expiryTime = expiryTime;
    }


    @Override
    protected StateEvent createCloneEvent(StreamEvent[] newEventStream, int eventState) {

        return new RemoveStateEvent(eventState, newEventStream, expiryTime);

    }

    @Override
    public long getExpiryTime() {
        return expiryTime;
    }


}