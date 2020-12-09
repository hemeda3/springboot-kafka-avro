# springboot-kafka-avro-test

## Half-Fully functional style  working  solution
Please checkout working-solution
`git checkout working-solution`
in this solution used `stream.to` inside BiConsumer to save join result
into CustomerBalance topic

## Fully funcational style - not working  solution
`git checkout main`
The optimal solution would be using BiFunction, fully
 functional  style , it half working it doesn't send 
 the message to CustomerBalance
