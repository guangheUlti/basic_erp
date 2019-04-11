package io.guangsoft.erp.controller;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class mainController {

    @RequestMapping("index")
    public String index() {
        return "index.html";
    }

    @RequestMapping("login")
    public String login() {
        return "login.html";
    }

    @RequiresRoles(value = {"admin","user"},logical = Logical.AND)
    @RequiresPermissions(value={"user:select","admin:select"},logical = Logical.OR)
    @RequestMapping("select")
    public String select() {
        return "manage/select.html";
    }

    @RequiresRoles("operator")
    @RequestMapping("update")
    public String update() {
        return "manage/update.html";
    }

    @RequiresPermissions("admin:delete")
    @RequestMapping("delete")
    public String delete() {
        return "manage/delete.html";
    }

}