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
   * Removes an event that is shadowed by a larger event. This is done to ease the process of 
   * splitting the tame ranges when {@code mergeOverlappingEvents()} runs.
   * @param events The events that may overlap.
   * @return ArrayList of merged events.
   */
  public ArrayList<Event> mergeOverlappingEvents(Collection<Event> events){
    ArrayList<Event> localEvents = new ArrayList<Event>(events);

    for(int i = 0; i < localEvents.size(); i++){
      for(int j = i+1; j < localEvents.size(); j++){
        if(localEvents.get(i).getWhen().overlaps(localEvents.get(j).getWhen())){
          System.out.println("Events overlap, will merge into single timeframe");

          int start1 = localEvents.get(i).getWhen().start();
          int start2 = localEvents.get(j).getWhen().start();
          int end1 = localEvents.get(i).getWhen().end();
          int end2 = localEvents.get(j).getWhen().end();


          int start = start1 < start2 ? start1 : start2;
          int end = end1 > end2 ? end1-1 : end2-1;

          Event tmpSet = localEvents.get(i);
          TimeRange newTimeRange = TimeRange.fromStartEnd(start, end, true);
          Event newEvent = new Event(tmpSet.getTitle(), newTimeRange, tmpSet.getAttendees());
          localEvents.set(i, newEvent);

          //Change time range for Event 1 to the "big" timerange, remove the other
          localEvents.remove(j);
        }
      }
    }
    return localEvents;
  }

  public ArrayList<TimeRange> splitTimeRange(Collection<TimeRange> timeRange, TimeRange toExclude){
    ArrayList<TimeRange> localTimeRange = new ArrayList<TimeRange>(timeRange);
    ArrayList<TimeRange> returnTR = new ArrayList<TimeRange>();
    Iterator<TimeRange> timeRangeIterator = localTimeRange.iterator();

    for(;timeRangeIterator.hasNext();){
      TimeRange tmp = timeRangeIterator.next();

      //The TimeRange toExclude is overlaps one of the available time ranges. 
      if(toExclude.overlaps(tmp)){
        System.out.println("OVERLAPS! " + toExclude.toString() + " overlaps with " + tmp.toString());




        //Exclude the TimeRange
        timeRangeIterator.remove();

        TimeRange t1 = TimeRange.fromStartEnd(tmp.start(), toExclude.start(), true);
        System.out.println("New TimeRange created " + t1.toString());
        TimeRange t2 = TimeRange.fromStartEnd(toExclude.end(), tmp.end(), true);
        System.out.println("New TimeRange created " + t2.toString());

        returnTR.add(t1);
        returnTR.add(t2);

      }else{
        return localTimeRange;
      }

    }
    return returnTR;
  }



  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    System.out.println("");
    System.out.println("NEW QUERY");
    ArrayList<TimeRange> possibleTimes = new ArrayList<TimeRange>();
    ArrayList<Event> localEvents = new ArrayList<Event>(events);



    final int MINUTES_IN_A_DAY = 1440;
    boolean[] availableTimes = new boolean[MINUTES_IN_A_DAY+1];

    Arrays.fill(availableTimes, false);

    if(request.getDuration() > MINUTES_IN_A_DAY){
      return possibleTimes;
    }


    //No atendees on the request
    if(request.getAttendees().size() == 0 && request.getOptionalAttendees().size() == 0){
      possibleTimes.add(TimeRange.WHOLE_DAY);
      System.out.println("RETURNING: ");
    for(TimeRange tr : possibleTimes){
      System.out.println(tr.toString());
    }
      return possibleTimes;
    }

    if(localEvents.isEmpty()){
      possibleTimes.add(TimeRange.WHOLE_DAY);
      System.out.println("RETURNING1: ");
    for(TimeRange tr : possibleTimes){
      System.out.println(tr.toString());
    }
      return possibleTimes;
    }

    for(Event event : events){
      System.out.println("ENTERING: ");
      //If event has no atendees involved in the request.
      if(!Collections.disjoint(event.getAttendees(), request.getAttendees())){
        System.out.println("Filling arr from " + event.getWhen().start() + " to " + event.getWhen().end());
        Arrays.fill(availableTimes, event.getWhen().start(), event.getWhen().end(), true);
      }else{
        localEvents.remove(event);
        System.out.println("Scheduled event has no atendees involved in the request...removing");
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
          System.out.println("New TR added: " + tr.toString());
        }
        intervalDuration = 0;
      }
    }

    if(availableTimes.length - intervalDuration  >= request.getDuration() && intervalDuration != 1){
      TimeRange tr = TimeRange.fromStartDuration(availableTimes.length - intervalDuration, intervalDuration-1); 
      System.out.println("New TR added at the end: " + tr.toString());
      System.out.println("Duration: " + intervalDuration);
      possibleTimes.add(tr);
    }

    if(localEvents.size() == 0){
      possibleTimes.add(TimeRange.WHOLE_DAY);
      return possibleTimes;
    }
    
    //System.out.println(Arrays.toString(availableTimes));
    System.out.println("RETURNING: ");
    for(TimeRange tr : possibleTimes){
      System.out.println(tr.toString());
    }
    return possibleTimes;
  }


}
