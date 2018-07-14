# Popular Movie Project (Stage 2)

## Overview
This project is apart of the Udacity Android Developer Nanodegree. In this project (stage 2 of 2), we were prompted to build an app to allow users to discover the most popular movies playing. This app improves the functionality of the app built in Stage 1. 

In the movie details view:
- Users are able to view and play trailers (in the youtube app or a web browser).
- Users are able to read reviews of a selected movie.
- Users are able to mark a movie as a favorite in the details view by tapping the "Add to Favorites" button. This is for a local movies collection stored on the phone.
- There is an additional pivot in the right hand corner menu that allows users to see all the "Favorited" movies. 

This application utilizes the [Room persistence library](https://developer.android.com/topic/libraries/architecture/room) with LiveData for the "Favorited" movies.

## Running the app
This app utilizes the [Movie Database API](https://developers.themoviedb.org/). To run this app, you must obtain a free API key from the MovieDB website and add this following line of code to your gradle.properties file:

`API_KEY = "<INSERT API KEY HERE>";` 

## The Application
![Screenshot](app.gif)

