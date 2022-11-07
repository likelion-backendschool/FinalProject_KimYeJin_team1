console.log('스크립트 실행여부');

    function SubmitForm(form){
    console.log('스크립트 실행여부');

    const editor = $(form).find('.toast-ui-editor').data("data-toast-editor");
    console.log(editor);
    const markdown = editor.getMarkdown();
    const html = editor.getHtml();
    form.content.value=markdown.trim();
    form.contentHTML.value=html;
    console.log(form.content.value);
    console.log(form.contentHTML.value);
    form.content.value = form.content.value.trim();
    if(form.content.value.length==0) {
            alert('내용을 입력해주세요');
    }
    if(form.contentHTML.value.length==0) {
            alert('내용을 입력해주세요');
    }
    //form.submit();
}
