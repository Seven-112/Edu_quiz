const functions = require('firebase-functions');
const firebase = require('firebase-admin');


var config = {
    apiKey: "AIzaSyAjWgHbX0iHQiwAT3c6_-pJNlp-vDWrAwE",
    authDomain: "edu-quiz-4c60e.firebaseapp.com",
    databaseURL: "https://edu-quiz-4c60e.firebaseio.com",
    storageBucket: "edu-quiz-4c60e.appspot.com"
  };
  
  

  
  firebase.initializeApp(config);

 
exports.create=functions.https.onRequest((req,res)=>{
 
  var data=JSON.parse(req.body);
  var user_id=data.user_id;
  firebase.database().ref("game_room/").orderByChild("availability").equalTo(1).limitToFirst(1).once('value', ((snapshot)=> {
     console.log(user_id);
	 console.log(snapshot.val());
    
    if(snapshot.val()!==null)
    {
		
		console.log("available");
		
      var childKey=Object.keys(snapshot.val());

      firebase.database().ref(`game_room/${childKey}`).update({
        "availability":2  , 
   
	  
      });
	   
	   firebase.database().ref(`game_room/${childKey}/${user_id}`).update({
			"status":true,
			"right":0,
			"wrong":0,
			"que_no":0,
			"sel_ans":"",
			
		});
		
	  console.log(snapshot.val());
    }
    else
    {
		console.log("not available");
				var ref=firebase.database().ref('game_room/');
			ref.push({
					"availability":1,    
        
					}).then(function(ref){
						console.log(ref.key);
						var key=ref.key;
							console.log(key);
						 var ref= firebase.database().ref(`game_room/${key}/${user_id}`).update({//.push({
									//ref.set({
								"status":true,
							"right":0,
							"wrong":0,
							"que_no":0,
							"sel_ans":"",
			
								});
		
		
						});

			
		
    }
    
    }));
    
   
  
 res.send({"code":user_id});
});