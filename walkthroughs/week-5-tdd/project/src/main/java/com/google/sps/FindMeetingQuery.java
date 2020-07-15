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


public final class FindMeetingQuery {

  public ArrayList<Event> mergeOverlappingEvents(Collection<Event> events){
    ArrayList<Event> localEvents = new ArrayList<Event>(events);

    for(int i = 0; i < localEvents.size(); i++){
      for(int j = i+1; j < localEvents.size(); j++){
        if(localEvents.get(i).getWhen().contains(localEvents.get(j).getWhen())){
          System.out.println("Events overlap, will merge into single timeframe");
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

        TimeRange t1 = TimeRange.fromStartEnd(tmp.start(), toExclude.start()-1, true);
        System.out.println("New TimeRange created " + t1.toString());
        TimeRange t2 = TimeRange.fromStartEnd(toExclude.end(), tmp.end()-1, true);
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
    ArrayList<Event> curatedEvents = new ArrayList<Event>(events);

    possibleTimes.add(TimeRange.WHOLE_DAY);
    
    //No atendees
    if(request.getAttendees().size() == 0 && request.getOptionalAttendees().size() == 0){
      return possibleTimes;
    }

    curatedEvents = mergeOverlappingEvents(curatedEvents);

    Iterator<Event> eventIterator = curatedEvents.iterator();
    for(;eventIterator.hasNext();){
      Event tmp = eventIterator.next();

      //Remove any events that do not conflict with the atendees.
      boolean atendeesDoNotConflict = Collections.disjoint(tmp.getAttendees(), request.getAttendees());
      if(atendeesDoNotConflict){
        System.out.println("Scheduled event has no atendees involved in the request...removing");
        eventIterator.remove();
        continue;
      }

      System.out.println("Splitting timeranges");
      for(TimeRange ps : possibleTimes){
        System.out.println(ps.toString());
      }
      possibleTimes = splitTimeRange(possibleTimes, tmp.getWhen());


    }




    System.out.println("Number of Meetings: " + events.size());

    for(Event e : events){
      System.out.println(e.getWhen().toString());
    }

    System.out.println("");

    return possibleTimes;
  }
}
