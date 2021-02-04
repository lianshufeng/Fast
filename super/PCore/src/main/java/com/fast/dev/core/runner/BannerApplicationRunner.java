package com.fast.dev.core.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.Arrays;

/**
 * 项目启动完毕后，打印banner
 */
public class BannerApplicationRunner implements ApplicationRunner {
    private final static String lines = "\n" +
            "        \\          Go!!!            /\n" +
            "         \\                         /\n" +
            "          \\                       /\n" +
            "           ]                     [    ,'|\n" +
            "           ]                     [   /  |\n" +
            "           ]___               ___[ ,'   |\n" +
            "           ]  ]\\             /[  [ |:   |\n" +
            "           ]  ] \\           / [  [ |:   |\n" +
            "           ]  ]  ]         [  [  [ |:   |\n" +
            "           ]  ]  ]__     __[  [  [ |:   |\n" +
            "           ]  ]  ] ]\\ _ /[ [  [  [ |:   |\n" +
            "           ]  ]  ] ]     [ [  [  [ :===='\n" +
            "           ]  ]  ]_]     [_[  [  [\n" +
            "           ]  ]  ]         [  [  [\n" +
            "           ]  ] /           \\ [  [\n" +
            "           ]__]/             \\[__[\n" +
            "           ]                     [\n" +
            "           ]                     [\n" +
            "           ]                     [\n" +
            "          /                       \\\n" +
            "         /                         \\\n" +
            "        /                           \\";

//    private String lines = "                _____________________\n" +
//            "               (<$$$$$$>#####<::::::>)\n" +
//            "            _/~~~~~~~~~~~~~~~~~~~~~~~~~\\\n" +
//            "          /~                             ~\\\n" +
//            "        .~                                 ~\n" +
//            "    ()\\/_____                           _____\\/()\n" +
//            "   .-''      ~~~~~~~~~~~~~~~~~~~~~~~~~~~     ``-.\n" +
//            ".-~              __________________              ~-.\n" +
//            "`~~/~~~~~~~~~~~~TTTTTTTTTTTTTTTTTTTT~~~~~~~~~~~~\\~~'\n" +
//            "| | | #### #### || | | | [] | | | || #### #### | | |\n" +
//            ";__\\|___________|++++++++++++++++++|___________|/__;\n" +
//            " (~~====___________________________________====~~~)\n" +
//            "  \\------___________[DZURL.TOP]_________-------/\n" +
//            "     |      ||         ~~~~~~~~       ||      |\n" +
//            "      \\_____/                          \\_____/";
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Arrays.stream(lines.split("\n")).forEach((line)->{
            System.out.println(line);
        });
    }
}
