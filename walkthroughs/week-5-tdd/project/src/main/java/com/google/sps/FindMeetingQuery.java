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

  private final int MINUTES_IN_A_DAY = 1440;

  /**
   * Returns an {@ArrayList} of {@code TimeRange}s of when an Event can take place.
   * Runtime: O(1) -> Regardless of the amount of events/attendees, a day will always
   * have the same number of minutes, which is what the array represents.
   * @param busyTimes boolean array containing all times where there is a meeting.
   * @param request Desired duration of the requested event
   * @return {@code ArrayList} with all possible timeranges for an event to happen.
   */
  private ArrayList<TimeRange> findTimeRanges(boolean[] busyTimes, long duration){
    ArrayList<TimeRange> possibleTimes = new ArrayList<TimeRange>();
    // Find free time in the array and if they fit the meeting, create the time range
    int availabilityInterval = 0;
    for(int minuteOfTheDay = 0; minuteOfTheDay < MINUTES_IN_A_DAY+1; minuteOfTheDay++){
      // If the minute is not busy, add a free minute to the counter.
      if(!busyTimes[minuteOfTheDay]){
        availabilityInterval++; 
      }else if(availabilityInterval != 0){
        if(availabilityInterval >= duration){
          TimeRange tr = TimeRange.fromStartDuration(minuteOfTheDay-availabilityInterval, availabilityInterval); 
          possibleTimes.add(tr);
        }
        availabilityInterval = 0;
      }
    }

    // If the whole day is available return a single TimeRange
    if(availabilityInterval == MINUTES_IN_A_DAY+1){
      possibleTimes.add(TimeRange.WHOLE_DAY);
      return possibleTimes;
    }

    // In case there is no event later, this will add the time since the last event until EoD.
    if(busyTimes.length - availabilityInterval  >= duration && availabilityInterval != 1){
      TimeRange tr = TimeRange.fromStartDuration(busyTimes.length - availabilityInterval, availabilityInterval-1); 
      possibleTimes.add(tr);
    }
    return possibleTimes;
  }

  /**
   * Returns {@code TimeRange} when a meeting can happen based on other events and the
   * meeting participants involved.
   * Runtime: O(N*M) -> The runtime of {@code query} will be proportional to the amount of 
   * events and attendees. This is seen when calling {@code Collections.disjoint} because it 
   * is called once for the mandatory attendees and once for the optional attendees and each time 
   * (events*possible attendees) 
   * @param events {@code Collection} of events happening.
   * @param request {@code MeetingRequest} for a meeting
   * @return Collection of {@code TimeRange} when a meeting can happen. 
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    ArrayList<TimeRange> possibleTimes = new ArrayList<TimeRange>();
    boolean[] busyTimesMandatoryAttendees = new boolean[MINUTES_IN_A_DAY+1];
    boolean[] busyTimesOptionalAttendees = new boolean[MINUTES_IN_A_DAY+1];

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
    if(events.isEmpty()){
      possibleTimes.add(TimeRange.WHOLE_DAY);
      return possibleTimes;
    }

    // For each event check if there is a conflict with the requested attendees. (N*M)
    for(Event event : events){
      // If a mandatory attendee is busy, the meeting can't happen, so time is blocked out on both arrays.
      if(!Collections.disjoint(event.getAttendees(), request.getAttendees())){
        Arrays.fill(busyTimesMandatoryAttendees, event.getWhen().start(), event.getWhen().end(), true); 
        Arrays.fill(busyTimesOptionalAttendees, event.getWhen().start(), event.getWhen().end(), true); 
      }else if(!Collections.disjoint(event.getAttendees(), request.getOptionalAttendees())){ 
        // If an optional attendee is busy, only the optional attendee schedule is blocked.
        Arrays.fill(busyTimesOptionalAttendees, event.getWhen().start(), event.getWhen().end(), true); 
      }
    }

    // If including the optionals doesn't yield any TimeRanges, do not include them.
    possibleTimes = findTimeRanges(busyTimesOptionalAttendees, request.getDuration());
    if(possibleTimes.size() == 0){
      return findTimeRanges(busyTimesMandatoryAttendees, request.getDuration());
    }
    return possibleTimes;
  }
}
