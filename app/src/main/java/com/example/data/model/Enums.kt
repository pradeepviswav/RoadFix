package com.example.data.model

enum class RoadType(val displayName: String, val badgeColorHex: Long) {
    NATIONAL_HIGHWAY("National Highway (NHAI)", 0xFF1E3A8A),
    STATE_HIGHWAY("State Highway (PWD)", 0xFF065F46),
    URBAN_MAIN_ROAD("Urban Main Road (Mpl Corp)", 0xFF7C2D12),
    RESIDENTIAL_STREET("Colony / Street", 0xFF475569),
    FLYOVER_BRIDGE("Flyover / Elevated Corridor", 0xFF581C87)
}

enum class PotholeSeverity(val label: String, val score: Int, val colorHex: Long) {
    MINOR("Minor Dip / Crack", 1, 0xFF3B82F6),
    MODERATE("Moderate Pothole", 2, 0xFFF59E0B),
    SEVERE("Severe Crater", 3, 0xFFEA580C),
    CRITICAL_HAZARD("Hazardous Trench (Critical)", 4, 0xFFDC2626)
}

enum class ReportStatus(val label: String, val stepIndex: Int, val colorHex: Long) {
    REPORTED("Reported", 0, 0xFF64748B),
    UNDER_REVIEW("Under Review", 1, 0xFFF59E0B),
    WORK_IN_PROGRESS("Work Started", 2, 0xFF2563EB),
    RESOLVED("Repaired & Fixed", 3, 0xFF10B981)
}

enum class NotificationType {
    STATUS_UPDATE,
    WORK_STARTED,
    RESOLVED,
    SENSOR_BUMP_DETECTED,
    DUPLICATE_ALERT
}
