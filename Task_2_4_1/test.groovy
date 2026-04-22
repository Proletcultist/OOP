
tasks {
    task("Task_1_1_1", "abobA", 2, "20.01.1999", "01.01.1970")
}

settings {
    gradeScale = [90: 5, 75: 4, 60: 3, 0: 2]
}

checkpoints {
    checkpoint("midterm" , "02.05.2006")
}

groups {
    group("ABOBS") {
        student("proletcultist", "Зенин Матвей Вадимович", "https://github.com/Proletcultist/OOP")
    }
}

assignments {
    assign("proletcultist", "Task_1_1_1")
}
