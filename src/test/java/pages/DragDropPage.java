package pages;

import components.DragDropArea;
import com.microsoft.playwright.Page;


// Реализована ленивая инициализация
public class DragDropPage extends BasePage {
    private DragDropArea dragDropArea;

    public DragDropPage(Page page) {
        super(page);
    }

    // Вызываем цепочкой: dragDropPage.dragDropArea().dragAToB();
    public DragDropArea dragDropArea() {
        if (dragDropArea == null) {
            dragDropArea = new DragDropArea(page);
        }
        return dragDropArea;
    }
}