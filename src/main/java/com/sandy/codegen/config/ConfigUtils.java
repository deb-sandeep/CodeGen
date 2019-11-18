package com.sandy.codegen.config;

import java.util.List ;
import java.util.Map ;
import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;

import ognl.Ognl ;

public class ConfigUtils {

    static final Logger log = Logger.getLogger( ConfigUtils.class ) ;
    
    public static final String INDENT1 = "   " ;
    public static final String INDENT2 = INDENT1 + INDENT1 ;
    public static final String INDENT3 = INDENT2 + INDENT1 ;
    
    private static final String CFG_VAR_PATTERN = "\\$\\{([^\\{]*)}+" ;

    public static String getFormattedMapContents( String name, 
                                                  Map<String, Object> map, 
                                                  String indent ) {
        
        StringBuilder builder = new StringBuilder() ;
        builder.append( indent + name + " -> {\n" ) ;
        for( String key : map.keySet() ) {
            builder.append( indent + INDENT1 + "[" + key + "] : " )
                   .append( map.get( key ) )
                   .append( "\n" ) ;
        }
        builder.append( indent + "}\n" ) ;
        return builder.toString() ;
    }

    public static String getFormattedListContents( List<? extends Object> list,
                                                   String indent ) {
        StringBuilder builder = new StringBuilder() ;
        for( Object listItem : list ) {
            builder.append( "\n" + indent + "- " + listItem ) ;
        }
        return builder.toString() ;
    }

    public static String enrichString( String input, CodeGenConfig config ) 
        throws Exception {
        
        if( StringUtil.isEmptyOrNull( input ) ) return input ;
        
        StringBuilder outputBuffer = new StringBuilder() ;
        
        Pattern r = Pattern.compile( CFG_VAR_PATTERN, Pattern.DOTALL ) ;
        Matcher m = r.matcher( input ) ;
        
        int lastEndMarker = 0 ;
        
        while( m.find() ) {
            int start = m.start() ;
            int end   = m.end() ;
            
            String processedString = processVar( input.substring( start, end ), config ) ;
            if( processedString != null ) {
                outputBuffer.append( input.substring( lastEndMarker, start ) ) ;
                outputBuffer.append( processedString ) ;
                lastEndMarker = end ;
            }
        }
        
        outputBuffer.append( input.substring(lastEndMarker, input.length() ) ) ;
        return outputBuffer.toString() ;

    }

    private static String processVar( String input, CodeGenConfig config ) 
            throws Exception {
        
        String var = input.substring( 2, input.length()-1 ) ;
        String enrichedVar = Ognl.getValue( var, config ).toString() ;
        return enrichedVar ;
    }
}
