# Solutions file

I am documenting all my solutions and thoughts as i go through the assignment.
Some of the decisions, trade-offs and known issues are also documented here.

## Part 1 - Cash Register

I'll start with the naive implementation for it, which is greedy algorithm - just pick up the biggest possible coin/bill and keep subtracting it from the total amount until it becomes zero.
It would definitely fail with arbitrary monetary values, e.g. 
* making 50 with 30, 25, 15 and 5 
* greedy solution would yield 30 + 15 + 5 => 50
* but the optimal solution is 25 + 25 => 50

Here comes the nice part though - for the majority of real world currencies and denominations, greedy algorithm works just fine. [1]
Since we have standardized denominations described in `Bill` and `Coin` enums, greedy solution will be sufficient.  

### Implementation notes

* Renamed `change` to `register` to better reflect the nature of money there
  * This was a private property anyways, so no breaking changes
* Needed to update the `:cashregiser` build.gradle to use the Java 21 version
  * Received a compile error otherwise

## Part 2 - Application 

To limit the scope a little bit i decided to do the following.

Application would mainly consist of a map, where the pins from the API will be displayed. 
Clicking a pin would open the bottom sheet window with the short information about the location.
It will also contain a big button "Purchase", which is currently inoperable.

I wanted to add the functionality of buying stuff using the `CashRegister` class that was built before, but i wanted to constrain myself to a one day of work. 
My estimation that a new screen with buy functionality would take around 4 more hours, so i decided to skip it for now.

### Nice to haves

* Display list of venues as well, not just map

### Tech stack
Architecture - MVVM
Database layer - Room
  * Mostly for storing amounts of money for each venue and user
Maps provider - Google Maps
  * For displaying the map and pins
Dependency injection - N/A
  * It's a very small app, so we'll skip it for now
  * Worst case we'll do a service locator pattern

### Implementation notes

* Ran an AGP upgrade since it makes sense to start a new app on a new version
* Ideally we'd want to separate domain entities from the view state entities / db entities, but this app doesn't follow it everywhere
  * Good example is `Venue` entity, which is used in all layers
  * Good solution would be to have `VenueEntity` for db, `VenueViewState` for view and `Venue` for domain and map between them
* Fetching location and venues should ideally be a responsibility of separate `DataSource`s and repository would just aggregate them
  * We'll skip it for now

## Closing notes
Overall i really enjoyed working on this assignment. Haven't had the opportunity to start a new project in a while, so it was refreshing.
Total time spent: around 8 hours on and off.
Looking forward to the feedback and the next steps, if any :)
Thanks!

## Links, sources
[1] The Greedy Coin Change Problem - https://arxiv.org/pdf/2411.18137v1