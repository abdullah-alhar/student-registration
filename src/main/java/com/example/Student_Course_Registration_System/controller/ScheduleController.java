package com.example.Student_Course_Registration_System.controller;

import com.example.Student_Course_Registration_System.model.Course;
import com.example.Student_Course_Registration_System.model.Lecturer;
import com.example.Student_Course_Registration_System.model.Room;
import com.example.Student_Course_Registration_System.model.Schedule;
import com.example.Student_Course_Registration_System.model.Registration;
import com.example.Student_Course_Registration_System.service.CourseService;
import com.example.Student_Course_Registration_System.service.LecturerService;
import com.example.Student_Course_Registration_System.service.RoomService;
import com.example.Student_Course_Registration_System.service.ScheduleService;
import com.example.Student_Course_Registration_System.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

@Controller
public class ScheduleController {

    @Autowired private ScheduleService scheduleService;
    @Autowired private CourseService courseService;
    @Autowired private LecturerService lecturerService;
    @Autowired private RoomService roomService;
    @Autowired private RegistrationService registrationService;

    @GetMapping("/schedules")
    public String getAllSchedules(Model model, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        String userId = (String) session.getAttribute("userId");
        model.addAttribute("userRole", role);
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", userId);
        
        List<Schedule> allSchedules = scheduleService.getAllSchedules();
        if ("STUDENT".equals(role)) {
            List<Registration> registrations = registrationService.getRegistrationsByStudentId(userId);
            List<Schedule> studentSchedules = new ArrayList<>();
            for (Schedule schedule : allSchedules) {
                for (Registration reg : registrations) {
                    if (schedule.getCourse().getCourseId().equals(reg.getCourse().getCourseId()) && "APPROVED".equals(reg.getStatus().name())) {
                        studentSchedules.add(schedule);
                        break;
                    }
                }
            }
            model.addAttribute("schedules", studentSchedules);
        } else if ("LECTURER".equals(role)) {
            model.addAttribute("schedules", scheduleService.getSchedulesByLecturerId(userId));
        } else {
            model.addAttribute("schedules", allSchedules);
        }
        
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("lecturers", lecturerService.getAllLecturers());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "schedule";
    }

    @PostMapping("/schedules/add")
    public String addSchedule(
            @RequestParam String courseId,
            @RequestParam String lecturerId,
            @RequestParam String day,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String roomId) {

        String scheduleId = "SCH" + System.currentTimeMillis();
        Course course = courseService.getCourseById(courseId);
        Lecturer lecturer = lecturerService.getLecturerById(lecturerId);
        Room room = roomService.getRoomById(roomId);
        Schedule schedule = new Schedule(scheduleId, course, lecturer, day, startTime, endTime, room);
        scheduleService.addSchedule(schedule);
        return "redirect:/schedules";
    }

    @PostMapping("/schedules/edit/{scheduleId}")
    public String updateSchedule(
            @PathVariable String scheduleId,
            @RequestParam String courseId,
            @RequestParam String lecturerId,
            @RequestParam String day,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String roomId) {

        Course course = courseService.getCourseById(courseId);
        Lecturer lecturer = lecturerService.getLecturerById(lecturerId);
        Room room = roomService.getRoomById(roomId);
        Schedule schedule = new Schedule(scheduleId, course, lecturer, day, startTime, endTime, room);
        scheduleService.updateSchedule(schedule);
        return "redirect:/schedules";
    }

    @GetMapping("/schedules/delete/{scheduleId}")
    public String deleteSchedule(@PathVariable String scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return "redirect:/schedules";
    }

    @GetMapping("/schedules/lecturer/{lecturerId}")
    public String getSchedulesByLecturer(@PathVariable String lecturerId, Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("schedules", scheduleService.getSchedulesByLecturerId(lecturerId));
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("lecturers", lecturerService.getAllLecturers());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "schedule";
    }

    @GetMapping("/schedules/course/{courseId}")
    public String getSchedulesByCourse(@PathVariable String courseId, Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("schedules", scheduleService.getSchedulesByCourseId(courseId));
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("lecturers", lecturerService.getAllLecturers());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "schedule";
    }
}