# SaViFer Students [ Android Studio App ]

This app was developed as a **Christmas 2024** project. It was a fun challenge to build during the holiday season and is designed to work as a students app that has various functions such as posts of news, weather, live chat, and a diary/agenda. 


> [!IMPORTANT]  
> This app does not represent neither contain real information about the center which is based "IES Sant Vicent Ferrer".
> I't s a project made to learn and improve my programming skills.


<h2>Login Activity</h2>

In the login activity we can find three fragments, the login, register and reset password fragment, they do as their name says. 
In order to work I use firebase authentication, which stores a email and a password.

This role has a 3 role system, they are "Administrator", "Teacher", Student.

| Role     | Can See The News | Button function                         |
|----------|-----------------|--------------------------------------------|
| Student  | Yes             | None                                           |
| Teacher  | Yes             | Can create posts                          |
| Admin    | Yes             | Can add professors with an email          |

The functions of the roles can be accessed via a a "Plus" button located in the posts area.

![image](https://github.com/user-attachments/assets/0198133d-cc62-4437-aedf-cdfaf289010e)

***

![LoginActivity](https://github.com/user-attachments/assets/496e5dbf-8a51-4b0d-8e10-b4ce080feab7)

<h2>Main Activity</h2>

In the main activity, the core functionality resides. This activity contains fragments for informative posts, chat, reminders, and settings, each providing a specific function.

<h3>Posts Fragment</h3>

***

In this fragment I make use of two API's, one mine created specifically for this app with Django, and the other one is open weather, an API used to get the weather in specific locations, in this case for the town of the students center (Algemesí).

The fragment is divided in two sections, the Weather and the News Posts.

![main_Activity](https://github.com/user-attachments/assets/0c9636dc-f352-4c8e-b493-d8c89b70ce6d)

***

If you click in a news post a pop up will appear with information about it (HTML formatted) about that post.
In case that you are a teacher you will be able to add posts.

<img src="https://github.com/user-attachments/assets/ec430ff5-b3ef-426b-99a8-5dfafda1e014" alt="AddPosts" width="600"/>


<h3>Chat & Settings Fragment</h3>

***

In the chat fragment every user no matter the role can send messages, in settings you can cahnge the color of your bubbles, no matter if you change your user nickname in settings your menssages will be displayed on the left, and the other people menssages will be on the right.

In the settings fragment you can change the user that will be displayed in the chat fragment, in order to other admins moderate and such the UID of the email is under the name, you can also change the bubble color. Offline mode does nothing for now.

![chat_fragment](https://github.com/user-attachments/assets/33e81eec-096e-44d6-a4ba-ab3097f0e3ea)

<h3>Reminders Fragment</h3>

***
This fragment allows you to schedule tasks with a title, description, day, and time. When the scheduled day and time match the current day and time, a notification will be sent to the phone's notification bar to remind you of the task. The app requires notification permissions to be granted in order to function properly. You can add as many tasks as needed to the list, and you also have the option to delete them.

![remindersFragment](https://github.com/user-attachments/assets/8c85a47a-0c8d-4d1c-8c65-0a31328a90d5)




<br>


<h2>App structure</h2>

| **Activities**       | **Fragments**              | **Other Components**    |
|-----------------------|----------------------------|--------------------------|
| Splash Activity       | Splash Fragment           | Chat Adapter            |
| Login Activity        | Login Fragment            | Posts Adapter           |
| Main Activity         | Register Fragment         | ChatMessage Class       |
|                       | Reset Password Fragment   | Task Class              |
|                       | Add Post Fragment         | Auth Manager            |
|                       | Settings Fragment         | Firestore Manager       |
|                       | Agenda Fragment           | Notification Worker     |
|                       | Chat Fragment             | Task Manager            |
|                       | Retrofit Fragment         | ApiResponsePost Class   |
|                       |                           | Post Class              |
|                       |                           | PostsService.kt         |
|                       |                           | RespuestaClima.kt       |
|                       |                           | RetrofitInstance        |
|                       |                           | RetrofitObject          |
|                       |                           | Constants.kt            |


