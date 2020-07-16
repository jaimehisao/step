// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays; 


public final class FindMeetingQuery {

  /**
   * Returns {@code TimeRange} when a meeting can happen based on other events and the
   * meeting participants involved.
   * @param events {@code Collection} of events happening.
   * @param request {@code MeetingRequest} for a meeting
   * @return Collection of {@code TimeRange} when a meeting can happen. 
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    final int MINUTES_IN_A_DAY = 1440;
    ArrayList<TimeRange> possibleTimes = new ArrayList<TimeRange>();
    ArrayList<Event> localEvents = new ArrayList<Event>(events);
    boolean[] availableTimes = new boolean[MINUTES_IN_A_DAY+1];

    //Meeting is longer than a day, that can't happen
    if(request.getDuration() > MINUTES_IN_A_DAY){
      return possibleTimes;
    }

    //No atendees on the request
    if(request.getAttendees().size() == 0 && request.getOptionalAttendees().size() == 0){
      possibleTimes.add(TimeRange.WHOLE_DAY);
      return possibleTimes;
    }

    //Clean schedule, all day is free
    if(localEvents.isEmpty()){
      possibleTimes.add(TimeRange.WHOLE_DAY);
      return possibleTimes;
    }

    for(Event event : events){
      //If event has no atendees involved in the request.
      if(!Collections.disjoint(event.getAttendees(), request.getAttendees())){
        Arrays.fill(availableTimes, event.getWhen().start(), event.getWhen().end(), true);
      }else{
        localEvents.remove(event);
      }
    }

    //Find free time in the array and if they fit the meeting, create the time range
    int intervalDuration = 0;
    for(int i = 0; i < availableTimes.length; i++){
      if(!availableTimes[i]){
        intervalDuration++;
      }else if(intervalDuration != 0){
        if(intervalDuration >= request.getDuration()){
          TimeRange tr = TimeRange.fromStartDuration(i-intervalDuration, intervalDuration); 
          possibleTimes.add(tr);
        }
        intervalDuration = 0;
      }
    }

    //Adds the time available towards the EoD
    if(availableTimes.length - intervalDuration  >= request.getDuration() && intervalDuration != 1){
      TimeRange tr = TimeRange.fromStartDuration(availableTimes.length - intervalDuration, intervalDuration-1); 
      possibleTimes.add(tr);
    }

    //If there is no event, then the whole day is free.
    if(localEvents.size() == 0){
      possibleTimes.add(TimeRange.WHOLE_DAY);
      return possibleTimes;
    }
    return possibleTimes;
  }
}
