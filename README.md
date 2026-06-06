# Kotlin Android Mobile Application
Academic Project — Kaunas University of Technology (2023)

## What it does
A native Android mobile application that fetches, stores, and 
displays signal data on an interactive grid map. Supports both 
online data fetching and offline local signal management.

## Features
- Fetches signal data from a remote API and stores it locally
- Offline-first architecture with local signal CRUD operations
- Interactive grid map displaying signal locations
- Background data sync with smart caching
- Bottom navigation with Home and Dashboard views
- Swipe-to-refresh for live data updates

## Tech Stack
Kotlin · Android Studio · Room · Retrofit · ViewModel · 
LiveData · Kotlin Coroutines · Navigation Component · 
RecyclerView · MVVM Architecture

## Architecture
- MVVM pattern with ViewModel and LiveData
- Repository pattern for data management
- Room database for local storage
- Retrofit for remote API communication
- AppContainer for dependency management
