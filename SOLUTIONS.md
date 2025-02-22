# Solutions file

I am documenting all my solutions and thoughts as i go through the assignment.
Some of the decisions, trade-offs and known issues are also documented here.

## Cash Register

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

## Links, sources
[1] The Greedy Coin Change Problem - https://arxiv.org/pdf/2411.18137v1