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

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comments")
public class DataServlet extends HttpServlet {

  private Comment comment;

  private final String ENTITY_NAME = "Comments";

  private final String NAME_PROPERTY = "name";
  private final String COMMENT_PROPERTY = "comment";
  private final String TIMESTAMP_PROPERTY = "timestamp";
  private final String UPVOTES_PROPERTY = "upvotes";
  private final String DOWNVOTES_PROPERTY = "downvotes";

  private final String ID_PROPERTY = "id";


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("application/json");
    
    
    Entity commentEntity = new Entity(entityName);

    String name = request.getParameter("name");
    String commentText = request.getParameter("comment");
    long timestamp = System.currentTimeMillis();
    commentEntity.setProperty(nameProperty, name);
    commentEntity.setProperty(commentProperty, commentText);
    commentEntity.setProperty(timestampProperty, timestamp);
    commentEntity.setProperty(upvotesProperty, 0);
    commentEntity.setProperty(downvotesProperty, 0);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    System.out.println("Added comment to datastore with " + name + " " + commentText + " on " + timestamp);

    response.sendRedirect("/index.html");
  }


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/html;");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    ArrayList<Comment> receivedComments = new ArrayList<>();
    //TODO: Change to sort by up/down vote when that is implemented.
    Query query = new Query(entityName).addSort(timestampProperty, SortDirection.DESCENDING); 
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
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    try{
      // Get key from request header
      Key key = KeyFactory.createKey(entityName, Long.parseLong(request.getHeader(idProperty)));
      datastore.delete(key);
    }catch(Exception exception){
      System.out.println("Error when deleting comment!");
      // Print error stacktrace - optional
      System.out.println(exception);
    }

    response.sendRedirect("/index.html"); // Redirect user to main page after comment.
  }

}
