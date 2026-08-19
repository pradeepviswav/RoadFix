package com.example.data.local

import com.example.data.model.AppNotification
import com.example.data.model.NotificationType
import com.example.data.model.PotholeReport
import com.example.data.model.PotholeSeverity
import com.example.data.model.ReportStatus
import com.example.data.model.RoadType

object InitialSeedData {
    val seedReports = listOf(
        PotholeReport(
            id = "REP-IND-1001",
            title = "Deep Crater Near Silk Board Junction",
            description = "Subgrade collapse on the main flyover down-ramp toward HSR Layout. Deep edge causing sudden swerving for two-wheelers during peak traffic.",
            latitude = 12.9176,
            longitude = 77.6233,
            address = "Hosur Road, Silk Board Junction, Outer Ring Rd",
            landmark = "Near HSR Flyover Ramp",
            city = "Bengaluru",
            state = "Karnataka",
            roadType = RoadType.URBAN_MAIN_ROAD,
            severity = PotholeSeverity.CRITICAL_HAZARD,
            status = ReportStatus.WORK_IN_PROGRESS,
            imageUrl = "sample_pothole_1",
            repairImageUrl = "sample_repair_1",
            repairNote = "BBMP Road Infra team deployed cold-mix asphalt patch unit. Work underway.",
            reportedByUserId = "usr_rahul_blr",
            reportedByUserName = "Rahul Sharma",
            timestamp = System.currentTimeMillis() - (86400000L * 2), // 2 days ago
            updatedTimestamp = System.currentTimeMillis() - 14400000L,
            upvotesCount = 38,
            isUpvotedByMe = true,
            aiVerified = true,
            aiConfidence = 0.98f,
            aiNotes = "AI Vision: High hazard crater detected (est depth: 14cm). Proximity to heavy vehicle lane increases accident probability.",
            isSyncPending = false,
            authorityAssigned = "BBMP South Zone (Bengaluru)"
        ),
        PotholeReport(
            id = "REP-IND-1002",
            title = "Severe Pothole Cluster on Western Express Highway",
            description = "Multiple broken asphalt craters near Andheri flyover after monsoon showers. Vehicles experiencing strong shock and tire damage.",
            latitude = 19.1136,
            longitude = 72.8697,
            address = "WEH, Near Gundavali Metro Station, Andheri East",
            landmark = "Opposite Metro Pillar 84",
            city = "Mumbai",
            state = "Maharashtra",
            roadType = RoadType.STATE_HIGHWAY,
            severity = PotholeSeverity.SEVERE,
            status = ReportStatus.UNDER_REVIEW,
            imageUrl = "sample_pothole_2",
            reportedByUserId = "usr_ananya_mum",
            reportedByUserName = "Ananya Desai",
            timestamp = System.currentTimeMillis() - (86400000L * 1),
            updatedTimestamp = System.currentTimeMillis() - 3600000L,
            upvotesCount = 24,
            isUpvotedByMe = false,
            aiVerified = true,
            aiConfidence = 0.95f,
            aiNotes = "AI Vision: Multiple alligator cracks & structural wearing course detachment detected.",
            isSyncPending = false,
            authorityAssigned = "BMC K-East Ward & MMRDA"
        ),
        PotholeReport(
            id = "REP-IND-1003",
            title = "Damaged Road Surface near Ring Road Nizamuddin",
            description = "Extensive surface wear and exposed aggregate stones creating skidding danger for two-wheelers.",
            latitude = 28.5912,
            longitude = 77.2588,
            address = "Mahatma Gandhi Marg, Ring Road, Nizamuddin East",
            landmark = "Near Sarai Kale Khan Inter-State Terminal",
            city = "Delhi-NCR",
            state = "Delhi",
            roadType = RoadType.URBAN_MAIN_ROAD,
            severity = PotholeSeverity.MODERATE,
            status = ReportStatus.RESOLVED,
            imageUrl = "sample_pothole_3",
            repairImageUrl = "sample_repair_3",
            repairNote = "PWD Central Division completed hot-mix asphalt resurfacing and line marking.",
            reportedByUserId = "usr_vikram_del",
            reportedByUserName = "Vikram Malhotra",
            timestamp = System.currentTimeMillis() - (86400000L * 5),
            updatedTimestamp = System.currentTimeMillis() - (86400000L * 1),
            upvotesCount = 49,
            isUpvotedByMe = true,
            aiVerified = true,
            aiConfidence = 0.92f,
            aiNotes = "AI Vision: Repaired condition verified. Smooth bitumen leveling detected.",
            isSyncPending = false,
            authorityAssigned = "Delhi PWD Road Maintenance Dept"
        ),
        PotholeReport(
            id = "REP-IND-1004",
            title = "NH-44 Hyderabad-Bangalore Highway Median Pothole",
            description = "High speed lane pothole on highway. Causes sudden braking at 80+ km/h. Urgent patching needed.",
            latitude = 17.2403,
            longitude = 78.4294,
            address = "National Highway 44, Shamshabad Bypass",
            landmark = "2km before RGIA Airport Exit",
            city = "Hyderabad",
            state = "Telangana",
            roadType = RoadType.NATIONAL_HIGHWAY,
            severity = PotholeSeverity.CRITICAL_HAZARD,
            status = ReportStatus.REPORTED,
            imageUrl = "sample_pothole_4",
            reportedByUserId = "usr_karthik_hyd",
            reportedByUserName = "Karthik Reddy",
            timestamp = System.currentTimeMillis() - 7200000L,
            updatedTimestamp = System.currentTimeMillis() - 7200000L,
            upvotesCount = 15,
            isUpvotedByMe = false,
            aiVerified = true,
            aiConfidence = 0.97f,
            aiNotes = "AI Vision: High velocity impact risk. Depth estimated at 12cm on express corridor.",
            isSyncPending = false,
            authorityAssigned = "NHAI Project Implementation Unit Hyderabad"
        ),
        PotholeReport(
            id = "REP-IND-1005",
            title = "Waterlogged Road Cavity on Anna Salai",
            description = "Rainwater filled trench hiding a severe pothole near Thousand Lights. Invisible hazard at night.",
            latitude = 13.0604,
            longitude = 80.2496,
            address = "Anna Salai (Mount Road), Thousand Lights",
            landmark = "Near Gemini Flyover approach",
            city = "Chennai",
            state = "Tamil Nadu",
            roadType = RoadType.URBAN_MAIN_ROAD,
            severity = PotholeSeverity.SEVERE,
            status = ReportStatus.WORK_IN_PROGRESS,
            imageUrl = "sample_pothole_5",
            repairImageUrl = "sample_repair_5",
            repairNote = "Greater Chennai Corporation emergency crew pumped water and installed pre-mix pavers.",
            reportedByUserId = "usr_suresh_chn",
            reportedByUserName = "Suresh Sundaram",
            timestamp = System.currentTimeMillis() - (86400000L * 1),
            updatedTimestamp = System.currentTimeMillis() - 1800000L,
            upvotesCount = 29,
            isUpvotedByMe = false,
            aiVerified = true,
            aiConfidence = 0.96f,
            aiNotes = "AI Vision: Submerged cavity detected with high risk score.",
            isSyncPending = false,
            authorityAssigned = "Greater Chennai Corporation (GCC)"
        )
    )

    val seedNotifications = listOf(
        AppNotification(
            id = "NOTIF-01",
            reportId = "REP-IND-1001",
            title = "Work Started: Silk Board Flyover Pothole",
            message = "BBMP Road Infrastructure team has commenced repair work at Hosur Road Silk Board Junction.",
            type = NotificationType.WORK_STARTED,
            timestamp = System.currentTimeMillis() - 14400000L,
            isRead = false
        ),
        AppNotification(
            id = "NOTIF-02",
            reportId = "REP-IND-1003",
            title = "Pothole Resolved & Repaired!",
            message = "Delhi PWD has completed asphalt patching on Ring Road Nizamuddin. Citizen confirmation active.",
            type = NotificationType.RESOLVED,
            timestamp = System.currentTimeMillis() - 86400000L,
            isRead = false
        ),
        AppNotification(
            id = "NOTIF-03",
            reportId = "REP-IND-1004",
            title = "Auto-Spotter Bump Logged",
            message = "Sensor spotted 2.8G shock bump on NH-44 Shamshabad. Tap to review and confirm report.",
            type = NotificationType.SENSOR_BUMP_DETECTED,
            timestamp = System.currentTimeMillis() - 7200000L,
            isRead = true
        )
    )
}
