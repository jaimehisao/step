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

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comments")
public class DataServlet extends HttpServlet {

  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private Comment comment;

  private final String datastoreName = "Comments";

  private final String nameProperty = "name";
  private final String commentProperty = "comment";
  private final String timestampProperty = "timestamp";
  private final String upvotesProperty = "upvotes";
  private final String downvotesProperty = "downvotes";

  private final String idProperty = "id";


  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("application/json");
    
    Entity commentEntity = new Entity(datastoreName);

    String name = null;
    String commentText = null;
    long timestamp = System.currentTimeMillis();
    commentEntity.setProperty(nameProperty, name);
    commentEntity.setProperty(commentProperty, commentText);
    commentEntity.setProperty(timestampProperty, timestamp);
    commentEntity.setProperty(upvotesProperty, 0);
    commentEntity.setProperty(downvotesProperty, 0);

    datastore.put(commentEntity);

    resp.sendRedirect("/index.html");
  }


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/html;");

    ArrayList<Comment> receivedComments = new ArrayList<>();
    //TODO: Change to sort by up/down vote when that is implemented.
    Query query = new Query(datastoreName).addSort(timestampProperty, SortDirection.DESCENDING); 
    PreparedQuery results = datastore.prepare(query);

    for(Entity entity : results.asIterable()){
      System.out.println(entity.getProperties()); //Debug output
      receivedComments.add(new Comment(entity));
    }

    Gson gson = new Gson();
    String jsonComments = gson.toJson(receivedComments);
    response.getWriter().println(jsonComments);
  }

  @Override 
  public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException{
    response.setContentType("application/json");

    try{
      // Get key from request header
      Key key = KeyFactory.createKey(datastoreName, Long.parseLong(request.getHeader(idProperty)));
      datastore.delete(key);
    }catch(Exception exception){
      System.out.println("Error when deleting comment!");
      // Print error stacktrace - optional
      System.out.println(exception);
    }

    response.sendRedirect("/index.html"); // Redirect user to main page after comment.
  }

}
