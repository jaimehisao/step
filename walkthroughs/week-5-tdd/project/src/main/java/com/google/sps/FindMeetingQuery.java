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

import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays; 


public final class FindMeetingQuery {

  private ArrayList<TimeRange> findTimeRanges(boolean[] busyTimes, MeetingRequest request){
    ArrayList<TimeRange> possibleTimes = new ArrayList<TimeRange>();
    // Find free time in the array and if they fit the meeting, create the time range
    int availabilityInterval = 0;
    for(int minuteOfTheDay = 0; minuteOfTheDay < busyTimes.length; minuteOfTheDay++){
      if(!busyTimes[minuteOfTheDay]){
        availabilityInterval++;
      }else if(availabilityInterval != 0){
        if(availabilityInterval >= request.getDuration()){
          TimeRange tr = TimeRange.fromStartDuration(minuteOfTheDay-availabilityInterval, availabilityInterval); 
          possibleTimes.add(tr);
        }
        availabilityInterval = 0;
      }
    }

    if(availabilityInterval == 1441){
      possibleTimes.add(TimeRange.WHOLE_DAY);
      return possibleTimes;
    }

    // Adds the time available towards the EoD
    if(busyTimes.length - availabilityInterval  >= request.getDuration() && availabilityInterval != 1){
      TimeRange tr = TimeRange.fromStartDuration(busyTimes.length - availabilityInterval, availabilityInterval-1); 
      possibleTimes.add(tr);
    }
    return possibleTimes;
  }

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
    boolean[] busyTimesMandatoryAttendees = new boolean[MINUTES_IN_A_DAY+1];
    boolean[] busyTimesOptionalAttendees = new boolean[MINUTES_IN_A_DAY+1];
    int eventsWithNoConflict = 0;

    // Meeting is longer than a day, that can't happen
    if(request.getDuration() > MINUTES_IN_A_DAY){
      return possibleTimes;
    }

    // No attendees on the request
    if(request.getAttendees().size() == 0 && request.getOptionalAttendees().size() == 0){
      possibleTimes.add(TimeRange.WHOLE_DAY);
      return possibleTimes;
    }

    // Clean schedule, all day is free
    if(localEvents.isEmpty()){
      possibleTimes.add(TimeRange.WHOLE_DAY);
      return possibleTimes;
    }

    for(Event event : events){
      // If a mandatory is busy, the meeting can't happen, so time is blocked out on both arrays.
      if(!Collections.disjoint(event.getAttendees(), request.getAttendees())){
        Arrays.fill(busyTimesMandatoryAttendees, event.getWhen().start(), event.getWhen().end(), true);
        Arrays.fill(busyTimesOptionalAttendees, event.getWhen().start(), event.getWhen().end(), true);
      }else if(!Collections.disjoint(event.getAttendees(), request.getOptionalAttendees())){
        // If an optional attendee is busy, only that schedule is blocked.
        Arrays.fill(busyTimesOptionalAttendees, event.getWhen().start(), event.getWhen().end(), true);
      }else{
        eventsWithNoConflict++;
      }
    }

    // If there is no event, then the whole day is free.
    if(localEvents.size()-eventsWithNoConflict == 0){
      possibleTimes.add(TimeRange.WHOLE_DAY);
      return possibleTimes;
    }
    possibleTimes = findTimeRanges(busyTimesOptionalAttendees, request);
    if(possibleTimes.size() == 0){
      return findTimeRanges(busyTimesMandatoryAttendees, request);
    }
    
    return possibleTimes;
  }
}
