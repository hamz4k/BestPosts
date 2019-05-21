# BestPosts : A unidirectional sate flow example App


# Features

- List of all posts 
- Displaying post detail
- List of all comments

# Architecture

A single state flow pattern (inspired by Kaushik Gopal's model Fragmented ep. 148 and 151) 
similar to MVI  was applied to a clean architecture approach.

The project has three modules :
  - App : Hosting presentation and UI and view state logic.
  - Domain : Hosting domain models and business rules transformation.
  - Data : Hosting data layer models and transformation to domain models, 
  as well as data querying and network related logic.

# Dependencies
  - RxJava2 : For reactive streaming and asynchronous operations 
  it was also used to setup the unidirectional data flow pipline between View, ViewModel, Model.
  - Dagger2 : for dependency injection.
  - Glide : For Image loading.
  
  # Testing
  
  - JUnit 4 : Testing framework.
  - Mokito : Mocking framework.
  - Truth : Test assertions.
  - MockWebserver : A webserver for testing api layer.

#  Future improvements

- The navigation between Post / Detail could benefit from a shared element transition with the post overview item in the list transitionning and expanding to its final position on the top of the Detail screen.
- Preserve scroll position on screen rotation.
- Add espresso end to end tests.
