package unportant.gist.csvparser

import spock.lang.Specification
import spock.lang.Unroll
import unportant.gist.csvparser.fsm.FsmParser

class ParserSpecification extends Specification {

    final def parser = new FsmParser(',' as char, '"' as char)

    @Unroll
    def "Parse #desc"(String desc, String line, List<String> expectedFields) {

        expect:
        parser.parse(line) == expectedFields

        where:
        desc                                |line                   || expectedFields
        "Single field"                      | 'abc'                 || ['abc']
        "Two fields"                        | 'ab,c'                || ['ab', 'c']
        "First field is empty"              | ',b'                  || ['', 'b']
        "Last field is empty"               | 'a,'                  || ['a', '']
        "Second field is empty"             | 'a,,c'                || ['a', '', 'c']
        "Single quoted field"               | '"ab"'                || ['ab']
        "Single quoted field with comma"    | '"a,b"'               || ['a,b']
        "Single quoted field with quote"    | '"a""b"'              || ['a"b']
    }

    @Unroll
    def "Parsing #line should throw an exception"(String line, String msg) {

        when:
        parser.parse(line)

        then:
        thrown(IllegalStateException.class)

        where:
        line                          | msg
        '"aa'                         | "fixme"
        '"aa,'                        | "fixme"
        '"aa"a"'                      | "fixme"
    }
}
