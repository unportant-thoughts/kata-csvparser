package unportant.gist.csvparser

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import unportant.gist.csvparser.fsm.FsmParser
import unportant.gist.csvparser.simple.SimpleParser

class ParserSpecification extends Specification {

    @Subject
    final def parser = new FsmParser(',' as char, '"' as char, 10)
//    final def parser = new SimpleParser(',' as char, '"' as char, 10)

    @Unroll
    def "#desc"(String desc, String line, List<String> expectedFields) {

        expect:
        parser.parse(line) == expectedFields

        where:
        desc                                |line                   || expectedFields
        "Empty line"                        | ''                    || ['']
        "Single field"                      | 'abc'                 || ['abc']
        "Two fields"                        | 'ab,c'                || ['ab', 'c']
        "First field is empty"              | ',b'                  || ['', 'b']
        "Last field is empty"               | 'a,'                  || ['a', '']
        "Second field is empty"             | 'a,,c'                || ['a', '', 'c']
        "Single quoted field"               | '"ab"'                || ['ab']
        "Single quoted field with comma"    | '"a,b"'               || ['a,b']
        "Single quoted field with quote"    | '"a""b"'              || ['a"b']
        "Empty quoted field"                | '""'                  || ['']
        "Last field empty after quote"      | '"a",'                || ['a','']
    }

    @Unroll
    def "Parsing #line should throw #exceptionClass"(String line, Class<? extends Exception> exceptionClass) {

        when:
        parser.parse(line)

        then:
        thrown(exceptionClass)

        where:
        line      || exceptionClass
        '"ab'     || IllegalStateException.class
        '"ab,'    || IllegalStateException.class
        '"ab"c"'  || IllegalStateException.class
        'a,"b'    || IllegalStateException.class
    }

    def "Successive calls"() {
        when:
        def field1 = parser.parse('a,b,c')
        def field2 = parser.parse('d')

        then:
        field1 == ['a', 'b', 'c']
        field2 == ['d']
    }
}
