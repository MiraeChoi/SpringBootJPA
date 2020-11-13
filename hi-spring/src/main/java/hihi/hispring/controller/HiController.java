package hihi.hispring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HiController {
    @GetMapping("hi")
    public String hihi(Model model) {
        model.addAttribute("data", "hihi~!");
        return "hihi";
    }

    @GetMapping("hi-mvc")
    public String hiMvc(@RequestParam("name") String name, Model model) {
        model.addAttribute("name", name);
        return "hi-template";
    }

    @GetMapping("hi-string")
    @ResponseBody
    public String hiString(@RequestParam("name") String name) {
        return "hi " + name;
    }

    @GetMapping("hi-api")
    @ResponseBody
    public Hihi hiApi(@RequestParam("name") String name, @RequestParam("id") String id) {
        Hihi hi = new Hihi();
        hi.setName(name);
        hi.setId(id);

        return hi;
    }

    static class Hihi {
        private String name;
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
