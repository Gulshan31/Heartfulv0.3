const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
// The Firebase Admin SDK to access the Firebase Realtime Database. 
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
// Listens for new messages added to messages/:pushId
exports.pushNotification = functions.database.ref('/News/{pushId}').onCreate( event => {

    console.log('Push notification event triggered');

    //  Grab the current value of what was written to the Realtime Database.
    var valueObject = event.data.val();
    
//     if (valueObject.previous) {
//      return;
//  }
    if(valueObject.Image != null) {
      valueObject.Image= "Sent you a photo!";
    }

  // Create a notification
    const payload = {
        notification: {
            title: ` ${valueObject.Title}`,
      body:"This is the notification send by jugaad ab kya kar mind hai zugadu dil sala chalu sone ke bhav me becna chahe aaloo",
            sound: "default"
        },
    };

  //Create an options object that contains the time to live for the notification and the priority
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };


    return admin.messaging().sendToTopic("pushNotifications", payload, options);
});