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

/*************************
 *     RANDOM FACT 
 ************************/

/**
 * Adds a random fact to the page.
 */
function randomFact() {
  const facts = ["My Google Drive contains over 22TB of files",
  "I'm currently watching The Office for the third time",
  "I have a turtle as a pet but I haven't seen it in a week",
  "Sometimes I worry about the amount of computers that I have.",
  "I low key hoard electronics and then regret not selling them when they were worth something",
  "My mom thinks my CS Degree (in progress) gives me magical Facebook powers.",
  "As a CS student I've been asked if I can hack a Facebook account more than about algorithms."];

  const fact = facts[Math.floor(Math.random() * facts.length)]; // Pick a random fact.

  const factContainer = document.getElementById('fact-container');  
  factContainer.innerText = fact; // Add it to the page.
}

/*************************
 *     Greeting
 ************************/
function greetingMaker() {
  const greetings = ["Bonjour!","Ahoj!","Hola!","Hello!","Guten Tag!", "Yasou!"];

  const greeting = greetings[Math.floor(Math.random() * greetings.length)]; // Pick a random greeting.

  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;  // Add it to the page.
}

/*************************
 *     Comments
 ************************/
 function covertCommentsToJSON(){
  const comments = document.getElementById("comments");
  const tmp = document.getElementById("comment-template");

  comments.innerHTML = "";
  comments.appendChild(template);
  
  for(const comment of payload){
    const insert = createCommentElement(comment);
    comments.append(insert);
    comments.append(document.createElement("br"));
  }
}

async function createComment(){
  const response = await fetch("/comments", {method: "PUT"});
  console.log("Comment created");
}

async function upvoteComment(){
  try{
    const response = await fetch("/comments/upvote-add", {method: "PUT", headers: {id}});
    queryComments();
  }catch(e){
    console.log("Error while upvoting comment! ".concat(e));
  }

}

async function downvoteComment(){
  try{
    const response = await fetch("/comments/downvote-add", {method: "PUT", headers: {id}});
    queryComments();
  }catch(e){
    console.log("Error while downvoting comment! ".concat(e));
  }
}

async function removeComment(id) {
  try {
    const response = await fetch("/comments", { method: "DELETE", headers: { id } });
    queryComments();
  } catch (e) {
    console.log("Error trying to delete comment!: ".concat(e));
  }
}

// ...javascript gives me a slight headache...

const addComments = async () => {
  const response = await fetch("/comments");
  const comments = await response.json();

  const commentsContainer = document.getElementById('comments-container');

  console.log(comments)

  for (const comment of comments) {
    const { timestamp, user, upvotes, text } = comment;

    commentsContainer.insertAdjacentHTML(
      'beforeend',
      `<article class="media">
        <div class="media-content">
          <div class="content">
            <p>
              <strong>${user}</strong>
              <br>
              ${text}
              <br>
              <small>${moment(timestamp).fromNow()}</small>
            </p>
          </div>
      </article>`
    );
  }
};

/*************************
 *     RANDOM FACT 
 ************************/
function createMap(){
  const map = new google.maps.Map(
    document.getElementById('map'),
    {center: {lat: 37.422, lng: -122.084}, zoom: 16});
  console.log('Created Map!')
}