# PunkAPI Export

Exports Brewdog [DIY Dog](https://www.brewdog.com/diydog) recipes from the [PunkAPI](http://punkapi.com) database
to [BeerXML](http://www.beerxml.com/) format, and maybe other formats eventually.

The BeerXML exported recipes are available here: https://github.com/stuartraetaylor/punkapi-beerxml

## Usage

1. Clone the repository and submodules.

        git clone --recursive https://github.com/stuartraetaylor/punkapi-export-java.git

2. Run the exporter (requires Java 8+).

        cd punkapi-export-java
        ./gradlew run

The recipes are output to the `beerxml` directory in the root of the project.

## Current state

At present the tool will import DIY Dog recipes from the [PunkAPI Database](https://github.com/samjbmason/punkapi-db/),
and then export the recipes to BeerXML format based on the [BeerXML v1 XSD](https://github.com/brewpoo/BeerXML-Standard/).

The exported BeerXML recipes currently contain only basic details for malts, hops, and yeast.
The intention at present is that the exported recipes will then be imported into your favourite brewing tool as starting point for your own clone recipes, filling in the missing details as necessary.

## TODO

Add better malt, hop, and yeast details to the exported recipes.

## Contributions

Pull requests and suggestions welcome.

## Acknowledgements

[punkapi-db](https://github.com/samjbmason/punkapi-db)

[BeerXML-Standard](https://github.com/brewpoo/BeerXML-Standard)

