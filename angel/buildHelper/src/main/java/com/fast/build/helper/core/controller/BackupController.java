package com.fast.build.helper.core.controller;

import com.fast.build.helper.core.helper.PathHelper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@RequestMapping("backup")
@Controller
public class BackupController {


    @Autowired
    private PathHelper pathHelper;

    @Autowired
    private CoreController coreController;

    @SneakyThrows
    @RequestMapping("")
    public void index(HttpServletRequest request, HttpServletResponse response) {
        response.sendRedirect("./backup/list");
    }

    @SneakyThrows
    @RequestMapping("list")
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();

        File[] fileList = pathHelper.getBackupPath("").listFiles();
        if (fileList == null) {
            fileList = new File[0];
        }

        List<File> files = new ArrayList<>(Arrays.asList(fileList));

        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o2.getName()) - Integer.parseInt(o1.getName());
            }
        });

        modelAndView.addObject("files", files);
        modelAndView.setViewName("backup_list");
        return modelAndView;
    }

    @SneakyThrows
    @RequestMapping("page/{fileName}")
    public ModelAndView page(HttpServletRequest request, HttpServletResponse response, @PathVariable("fileName") String fileName) {
        File filePath = pathHelper.getBackupPath(fileName);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("backup_info");
        modelAndView.addObject("fileName", fileName);
        if (filePath.exists()) {
            modelAndView.addObject("files", this.coreController.listFile(request, filePath));
        }
        return modelAndView;
    }


}
