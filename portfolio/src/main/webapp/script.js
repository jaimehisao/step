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
    "I have a turtle as a pet but I haven't seen it in a week"];

  // Pick a random greeting.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

/*************************
 *     Greeting
 ************************/
function greetingMaker() {
  const greetings = ["Bonjour!","Ahoj!","Hola!","Hello!","Guten Tag!", "Yasou!"];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}


/*************************
 *   COMMENTS SECTION 
 ************************/

 function comments(){

 }

 function deleteMessage(){

 }

 function validateComment(){
   
 }



 /**
  * Funtions that run on page load
  */
 function onPageLoad(){

 }


