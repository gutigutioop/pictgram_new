package com.example.pictgram.controller;

import java.util.Locale;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.pictgram.entity.Comment;
import com.example.pictgram.form.CommentForm;
import com.example.pictgram.repository.CommentRepository;

@Controller
public class CommentsController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CommentRepository repository;

    @RequestMapping(value = "/topics/{topicId}/comments/new")
    //local ブラウザに言語設定ある。位置の事。どの言語か持ってるブラウザの言語に設定される。URLの中に入ってるtopics/○○/comments
    //の番号を@PathVariableを使ってロング型のtopicid変数にいれる。文字列なのでlongにいれる。
    public String newComment(@PathVariable("topicId") long topicId, Model model) {
        CommentForm form = new CommentForm();
        form.setTopicId(topicId);
        model.addAttribute("form", form);

        return "comments/new";
    }

    @RequestMapping(value = "/topics/{topicId}/comment")
    public String create(@PathVariable("topicId") long topicId, @Validated @ModelAttribute("form") CommentForm form,
            BindingResult result, Model model, RedirectAttributes redirAttrs, Locale locale) {
        if (result.hasErrors()) {
            model.addAttribute("hasMessage", true);
            model.addAttribute("class", "alert-danger");
            model.addAttribute("message", messageSource.getMessage("comments.create.flash.1", new String[] {}, locale));
            //<form class="new_comment" id="new_comment" th:action="@{/topics/__${form.topicId}__/comment}" th:object="${form}"> comments.html参照
            //th:action="@{/topics/__${form.topicId}__/comment}のtopicIdが４２行目に入る。
            //layout.htmlの<div th:if="${hasMessage}" class="alert alert-dismissible fade show" th:classappend="${class}" role='alert'>
            //trueなら・・・の処理が入る。コメントに誤りがあったらバリデーションエラーが発生。"class"は決まってる言語。コメント書き直せって元の画面に伝える。alert-dangerで赤文字入る。
            return "comments/new";
            
        }
            //mapper map(地図）同じ名のデータをエンティティに詰め込む。リクエストも？画面から飛んできたformクラス→コントローラー
        Comment entity = modelMapper.map(form, Comment.class);
        entity.setTopicId(topicId);
        repository.saveAndFlush(entity);

        redirAttrs.addFlashAttribute("hasMessage", true);
        redirAttrs.addFlashAttribute("class", "alert-info");
        redirAttrs.addFlashAttribute("message",
                messageSource.getMessage("comments.create.flash.2", new String[] {}, locale));

        return "redirect:/topics";
        //TopicsControllerのindexメソッドを呼ぶ。新しいページ作るのめんどいからtopicsに任せる。@GetMapping(path = "/topics")のindexメソッドを呼ぶ。
    }

}