# Elepy Query Language

Elepy Query Language is a query language designed to interact with the Elepy Query Language model for data retrieval and
filtering. It provides
a flexible and intuitive way to construct queries that allow users to extract specific data based on various conditions
and criteria.

With Elepy Query Language, users can compose powerful queries to retrieve, filter, and manipulate data stored in a
compatible system
or database. The language supports a wide range of operations, including logical operations, filtering based on
different data types, and search capabilities.

## Expressions

Expressions are the basis of the Elepy Query Language. They are the building blocks of the query language.

Expressions are either:

- [Search Queries](#search-queries)
- [Filters](#filters)
- [Logical Operations](#logical-operations)

### Search Queries

Search queries are the most basic form of expression. They are simply a string of text.
It is important to note that the search mechanism is different for each `Crud` implementation. Therefor, it's important
to know how your database handles search queries, as results may vary per database. *Not all `Crud` implementations
support search queries.*

Example Search Query: `bob marley`

### Filters

Filters enable you to narrow down the data based on specific conditions. There are three types of filters available:

- [Basic Filters](#basic-filters)
- [Text Filters](#text-filters)
- [Number Filters](#number-filters)

#### Basic Filters

Basic filters are the most basic form of filtering. They're basically equality operators. All `Crud` implementations
*must* support basic filters.

Equals:

- `name = bob`
- `name == bob`
- `name: bob`
- `name eq bob`
- `name equals bob`

Not equals:

- `name != bob`
- `name neq bob`
- `name not equals bob`
- `name not equal bob`
- `name not eq bob`

#### Text Filters

Text filters are filters that are only applicable to text properties. Some `Crud` implementations may not support all
text filters.

Contains:

- `name contains bob`
- `name contains "bob marley"`

Starts with:

- `name starts with bob`
- `name starts with "bob marley"`
- `name sw bob marley`

#### Number Filters

Number filters are filters that are only applicable to number properties. Some `Crud` implementations may not support
all number filters.

### Logical Operations

Binary operations are operations that combine two other expressions. All `Crud` implementations *must* support logical
operations.

There are two types of logical operations:
And:

- `name = bob and age = 12`
- `name = bob && age = 12 && a search term`

Or:

- `name = bob or age = 12`
- `name = bob || age = 12 || a search term`

Expressions can be combined:

- `name = bob and age = 12 or name = marley`
- `name = bob && age = 12 || name = marley`

Expressions can be grouped by wrapping them in parentheses:

- `(name = bob and age = 12) or name = marley`

### Escaping Expression

In cases where your search query or filter uses a reserved keyword, you can escape it by wrapping it in single/double
quotes.

Example:

- `name = "bob equals marley"`
- `"a sample search query"`
- `name = 'bob equals marley' and "a sample and search query"`
