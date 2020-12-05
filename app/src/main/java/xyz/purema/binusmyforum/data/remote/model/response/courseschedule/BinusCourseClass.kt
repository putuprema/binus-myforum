package xyz.purema.binusmyforum.data.remote.model.response.courseschedule

class BinusCourseClass(
    var courseCode: String,
    var courseType: String,
    var courseClass: String,
    var eventCourseSchedule: MutableList<BinusCourseSchedule>
)