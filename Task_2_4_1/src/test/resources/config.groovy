
tasks {
    task("Task_1_1_1", "Heap Sort", 2, "20.08.1999", "01.10.2025")
    task("Task_1_1_2", "Blackjack", 3, "09.12.2025", "01.11.2025")
    task("Task_1_2_2", "abobA", 1, "09.12.2025", "01.01.2026")
}

settings {
    gradeScale = [90.0: "5", 75.0: "4", 60.0: "3", 0.0: "2"]
}

checkpoints {
    checkpoint("midterm" , "01.01.2026")
    checkpoint("final" , "01.06.2026")
}

groups {
    group("24214") {
        student("proletcultist", "Зенин Матвей Вадимович", "https://github.com/Proletcultist/OOP")
        student("chebupelka", "Токарев Максим Константинович", "https://github.com/chebupelka332-pro/OOP")
    }
}

include "proletcultist.groovy"
include "chebupelka.groovy"
