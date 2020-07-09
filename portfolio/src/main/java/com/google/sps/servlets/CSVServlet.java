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

package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.util.logging.Logger;


@WebServlet("/markers")
public class CSVServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");

    List<CityCoords> coords = new ArrayList<>();

    try{
        Scanner scanner = new Scammer(getServletContext().getResourceAsStream("assets/markers.csv"));

        while(scanner.hasNextLine()){
            String tmp = scanner.nextLine();
            String dividedLine = tmp.split(",");

            String cityName = dividedLine[0];
            double latitude = Double.parseDouble(dividedLine[1]);
            double longitude = Double.parseDouble(dividedLine[2]);

            coords.add(new CityCoords(cityName, latitude, longitude));
        }
        Gson gson = new Gson();
        String toSend = gson.toJson(coords);
        response.getWriter().println(toSend);

    }catch(Exception e){
        System.out.println("CSV file containing city coordinates could not be found!")
    }



    }

}
