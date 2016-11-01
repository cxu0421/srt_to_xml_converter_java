Skip to sidebar navigation
Skip to content
Linked Applications
Bitbucket
Projects
Repositories
Find a repository...
Find a repository...
Help
Logged in as Xu, Calvin (NIH/NLM) [C] (xuzc)
Digitalrepository
1
Digitalrepository
digitalrepository
Source
Branchrelease/-R2.8.1Branch actions	
digitalrepository/java_apps/dr.preingest/SRT_TO_DFXP_Converter/src/SRT_TO_DFXP_Converter.java
Xu, Calvin (NIH/NLM) [C]Xu, Calvin (NIH/NLM) [C] committed 3cfc058aad512 Sep 2016
BlameRaw file
Source viewDiff to previousHistory

127
​
128
    private String convertToXML() {
129
        String dfxp = "";
130
        dfxp += header;
131
        dfxp += "     <head>\n";
132
        dfxp += "          <styling>\n";
133
        if (styleXMLString != null) {
134
            dfxp += "              " + styleXMLString;
135
        } else {
136
            dfxp += "              <style id=\"1\" tts:backgroundColor=\"black\"  tts:fontFamily=\"Arial\" tts:fontSize=\"14\" tts:color=\"white\" tts:textAlign=\"center\" tts:fontStyle=\"Plain\" />\n";
137
        }
138
        dfxp += "          </styling>\n";
139
        dfxp += "     </head>\n";
140
        dfxp += "     <body>\n";
141
        if (styleXMLString != null) {
142
            dfxp += "          <div xml:lang=\"en\" style=\"default\">\n";
143
        } else {
144
            dfxp += "          <div xml:lang=\"en\">\n";
145
        }
146
        for (int i = 0; i < node_list.size(); i++) {
147
            dfxp += "               <p begin=\"" + node_list.get(i).begin_time
148
                    + "\" ";
149
            dfxp += "end=\"" + node_list.get(i).end_time
150
                    + "\" style=\"1\">\n";
151
            for (int k = 0; k < node_list.get(i).content.size(); k++) {
152
                dfxp += "                    "
153
                        + node_list.get(i).content.get(k);
154
                if ((node_list.get(i).content.size() > 1)
155
                        && (k != node_list.get(i).content.size() - 1)) {
156
                    dfxp += "<br />\n";
157
                } 
158
            }
159
            dfxp += "               </p>\n";
160
        }
161
        dfxp += "          </div>\n";
162
        dfxp += "     </body>\n";
163
        dfxp += "</tt>\n";
164
        return dfxp;
165
    }
166
​
167
    private void writeXML(String dfxp) {
168
        BufferedWriter out = null;
169
        try {
170
            out = new BufferedWriter(new FileWriter(output_file));
171
            out.write(dfxp);
172
            out.close();
173
        } catch (IOException e) {
174
            // TODO Auto-generated catch block
175
            error = "Error Writing To File";
176
            System.out.println("Error Writing To File\n");
177
            System.exit(0);
178
        }
179
    }
180
​
181
    public void doAll() {
182
        try {
183
            node_list = readSRT();
184
            writeXML(convertToXML());
185
        } catch (Exception e) {
186
            error = "Incorrectly Formatted SubScript File";
187
        }
188
    }
189
​
190
    public static void main(String[] args) {
191
        if ((args.length < 2) || (args[1].equals("-h"))) {
192
            System.out.println("\n<---   SRT to DFXP Converter Usage   --->");
193
            System.out
194
                    .println("Conversion: java -jar SRT_TO_DFXP.jar <input_file> <output_file> [-d]");
195
            System.out
196
                    .println("Conversion REQUIRES a input file and output file");
197
            System.out.println("[-d] Will Display XML Generated In Console");
198
            System.out.println("Help: java -jar SRT_TO_DFXP.jar -h");
199
        } else if (!(new File(args[0]).exists())) {
200
            System.out.println("Error: Input SubScript File Does Not Exist\n");
201
        } else {
202
            // SRT_TO_DFXP converter=new SRT_TO_DFXP(args[0], args[1]);
203
            SRT_TO_DFXP_Converter converter = new SRT_TO_DFXP_Converter(
204
                    new File(args[0]), new File(args[1]));
205
            converter.node_list = converter.readSRT();
206
            String dfxp = converter.convertToXML();
207
            if ((args.length == 3) && (args[2].equals("-d")))
208
                System.out.println("\n" + dfxp + "\n");
209
            converter.writeXML(dfxp);
210
            System.out.println("Conversion Complete");
211
        }
212
    }
213
}
