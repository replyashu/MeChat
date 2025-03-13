Me Chat is a single chat application based on firebase

**Login**
Currently Using onTapSignIn Google firebase login

**Logout**
**User can logout from the app by clicking on the logout button

**Delete User**
If user want to delete his/ her account, he can do it by clicking on delete account button, similar to deactivate profile

**HomeScreen**
On Home Page, user would see his/ her name, followed by two buttons as stated above- logout and delete user
To initiate chat, we need to have receiver, so a LazyRow is being used to show all the users of the app available in horizontal orientation
There will be list of chats in which only last chat between pair (current user -> receiver and vice versa) will be shown

HomeScreen consists of Contact Chips and recent chats (only last chat for unique user)

To initiate new chat, user can click on any contact and start the chat
Or, click on existing chats and user can continue chat there

**Handling message states**
-------------------------
There are three states of messages- Pending, Sent and read (skipping delivered as user has to open the app, means it will be marked as read not delivered)
Once user click on the send button, state of message will be recorded as PENDING
Once message is synced with firebase, state of message will be updated as SENT
Once receiver reads the message, state of message will be updated as READ

**Handling isTyping**
-------------------
When user starts typing, status would be updated as <User> is typing and will be shown to the receiver
If there is an inactivity (not typing for 2 seconds) or there is no internet, in that case isTyping would be marked as false and nothing would be shown to receiver

**Components and Architecture**
-----------------------------
Used MVVM with clean architecture
Room DB for local storage
Firebase for chats and user authentication
Hilt for dependency injection
Composables and Navigation components for screens and navigation
Mockito, JUnit for testing

**Structure of the Chat node is**
-------------------------------
    senderId
    senderName
    receiverName
    receiverId
    message
    timestamp
    status
    isSynced
Stored both in room db and firestore database

Relation to make sure we show only relevant chats to sender-receiver pair is either senderId == receiverId && receiverId == senderId or vice versa
There is also typing status and users table maintained on firebase

**Testing**
---------
MessageDaoTest- it covers test cases for Room db interactions for inserting and updating messages
ChatViewModelTest- it covers interaction with usecases for sending/ updating messages, status and timestamps between sender and receiver

**Structure**
----------
- app
    - src
      - main
         - chat
           - data
           - repository
             - db
               - AppDataBase
               - MessageDao
             - ChatRepository
           - ui
             - components
               - UserList
               - ContactChip <Collapsible, click on user and it will toggle the expand/ collapse list>
             - screens
               - ChatListScreen <Will show list of last chats with unique user on homepage>
               - ChatScreen
               - ContactScreen
           - ChatUseCase
           - ChatViewModel
         - global
           - Constants
           - AppModule
           - Extension
           - Utils
           - Application
         - login
         - ui
         - NavigationComponent
         - MainActivity

**Steps To Run**
-------------
Will attach apk in github release itself
Click on google button, that would invoke google sign in, if nothing happens, kindly kill the app and try again or clear playstore cache <Common problem with onetap sign in of too many requests, in dev mode>
Once logged in, you would see your name and logout and delete account options
List of users available, which are users of this app would appear under user button, use it to toggle its visibility
Recent chat list would be visible below that
Click on any user or chat (if available) to send/ chat with the other user

**Trade-offs and future improvements**
------------------------------------
Group chat -This implementation is for one-one chat for the users, to make it work for group chat, need to add one relation field in message, which will have list of sender/ receiver id's to which those messages would be shown
There would be a separate relation table which would work as identifier of the group and chats would have same chatid for a group

**UI**
----
1. For ease, showing all users on homepage itself, if this has to scale, there has to be a floating button, which can show the user list either as chips or lazycolumn list
2. Sign out and Deactivate can go in profile page
3. Orientation of messages (self on left, others on right, coloring the messages accordingly)
4. Loader when messages and contacts are loading (With firebase it wont take much time as both list would be short initially)

**Own APIs**
--------
Instead of using firebase, we could use our own APIs

**Support for Image/ Files**
------------------------
If we had to do the same with firebase, need to upload image/ file to firebase storage, git its reference and store it in message node- both room and firebase
If we are doing through APIs, we can send the image/ file as multipart to our server and it can be saved in s3 bucket, with reference to it stored in message node again

**Support for Emojis and animations**
---------------------------------
Future version can have support for emojis and animations too

**Support for Audio/ Video calls**
------------------------------
User can have option to call other user either individually or in a defined set of group



