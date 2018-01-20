readTextFile(File, Text) :-
  require(
    fileExpected(File),
    (exists_file(File), open(File, read, Input, []))
  ),
  read_stream_to_codes(Input, Text),
  close(Input).

readTextFileLines(File, L) :-
  require(
    fileExpected(File),
    (exists_file(File), open(File, read, Input, []))
  ),
  getLines(Input, L),
  close(Input).

getLines(Input, L):-
  read_line_to_codes(Input, H),
  (   H == end_of_file
  ->  L = []
  ;   L = [H|T],
      getLines(Input,T)
  ).

readTermFile(File, Term) :-
  require(
    fileExpected(File),
    (exists_file(File), open(File, read, Input, []))
  ),
  require(
    fileWithPrologTermExpected(File),
    read(Input, Term)
  ),
  close(Input).

readJSONFile(File, Json) :-
  require(
    fileExpected(File),
    (exists_file(File), open(File, read, Input, []))
  ),
  require(
    fileWithJSONExpected(File),
    json_read(Input, Json, [])
  ),
  close(Input).

readXMLFile(File, Xml) :-
  require(
    fileExpected(File),
    (exists_file(File), open(File, read, Input, []))
  ),
  require(
    fileWithXMLExpected(File),
    load_xml(File, Xml, [])
  ),
  close(Input).

writeTextFile(File, Text) :-
  open(File, write, Output, []),
  format(Output, '~s', [Text]),
  close(Output).

writeTermFile(File, Term) :-
  open(File, write, Output, []),
  format(Output, '~q.', [Term]),
  close(Output).

writeJSONFile(File, Json) :-
  open(File, write, Output, []),
  json_write(Output, Json, [width(1)]),
  close(Output).

dcgAcceptor(G, X) :- apply(G, [X, []]).

dcgParser(G, X, Y) :- apply(G, [Y, X, []]).
