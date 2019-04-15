# Color to hex (polyglot)
The `COLOR_TO_HEX` UDF is a Polyglot UDF that executes ruby code in order to transform colors to hex codes. This is experimental.

# Prerequisites
- KSQL server running on GraalVM (tested using rc15)
- Truffleruby and `chroma` gem
  ```bash
  $ gu install ruby
  $ gem install chroma
  ```
  
  _Note:_ if you are using RVM, be sure to mount truffleruby.
  ```bash
  $ rvm mount /path/to/graal/jre/languages/ruby/ -n truffleruby
  $ rvm use truffleruby
  ```

# Example usage
```sql
 SELECT color, color_to_hex(color) FROM SOME_STREAM ;

# sample output
deepskyblue, #00bfff
peachpuff, #ffdab9
ghostwhite, #f8f8ff
seagreen, #2e8b57
```
