'''The scheduler module contains the function schedule used to solve the
scheduling problem

Author: miggoncan2
'''

import sys
import logging
import logging.config
import calendar as calendarLib
import datetime

try:
    from ortools.sat.python import cp_model
except ImportError:
    print('ERROR: Could not find module ortools. Try \'pip install ortools\'')
    sys.exit(1)


# This dict will be used to convert from a day str to its int 
# representation
WEEK_DAY = {
    'Monday': 0,
    'Tuesday': 1,
    'Wednesday': 2,
    'Thursday': 3,
    'Friday': 4,
    'Saturday': 5,
    'Sunday': 6
}

# This dict will be used to convert from the int representation of a
# weekday to its human readable f
DAY_NUM_TO_WEEK_DAY = {dayNum: weekdayName 
                        for weekdayName, dayNum in WEEK_DAY.items()}

# These variables are used to identify the BoolVars used in the CpModel
SHIFT = 's'
CONSULT = 'c'

# Default weights of the objective function
# Do NOT change this values here. Instead, use the configuration dict
# supplied to the schedule function.
# This dict can be easily created from the config/scheduler.json file
DEFAULT_CYCLE_SHIFT_RATE = 10
DEFAULT_WANTED_SHIFT_WEIGHT = 3
DEFAULT_UNWANTED_SHIFT_WEIGHT = 3
DEFAULT_WANTED_CONSULTATION_WEIGHT = 3
DEFAULT_ALL_SHIFT_WEIGHT = 1
DEFAULT_CONSULTATION_WEIGHT = 1


def getShiftPreferences(*, shiftConfs, dayConfs, keys, daysOfMonth):
    '''Obtain the shift preferences indicated by keys

    Day shift preferences indicated by dayConfs have higher preference
    than the ones indicated by shiftConfs

    If keys are not present in the shiftConfs or in dayConfs, a default
    empty list will be used as their values

    Keyword Args:
        shiftConfs:
            A list of dicts representing the shift configuration of the 
            doctors. Each dict must have a 'doctorId' key whose value 
            is the id of the corresponding doctor, and both keys 
            indicated by the keyword argument 'keys'. The value of the 
            'keys' must be iterables of dicts, where each dict has to 
            have the key 'shift'. The value of each 'shift' is a day of 
            the week.

            Example: (suppose keys = ['wantedShifts', 'unwantedShifts'])

                [
                    {
                        'doctorId': 1,
                        'wantedShifts': [
                            {'shift': 'Monday'},
                            {'shift': 'Tuesday'}
                        ],
                        'unwantedShifts': [
                            {'shift': 'Friday'}
                        ]
                    },
                    {
                        'doctorId': 3,
                        'wantedShifts': [
                            {'shift': 'Thursday'}
                        ],
                        'unwantedShifts': []
                    },
                    {
                        'doctorId': 5,
                        'wantedShifts': [],
                        'unwantedShifts': [
                            {'shift': 'Wednesday'}
                        ]
                    },
                ]

        dayConfs:
            A list of dicts representing the configuration of each day
            of the month whose schedule is to be generated. Each dict
            must have both keys indicated by the keyword argument 
            'keys'. The value of the 'keys' must be an iterable of dict,
            where each dict has to have a pair 'id': int. The int is 
            the id of a doctor.

            The first element of the list should correspond with the
            configuration of the first day of the daysOfMonth list, the 
            second element with the configuration of the second day, 
            and so on.

            The list is assumed to have the same size as daysOfMonth

            Example: (suppose keys = ['wantedShifts', 'unwantedShifts'])

            [
                {
                    'wantedShifts': [
                        {'id': 1},
                        {'id': 3}
                    ],
                    'unwantedShifts': [
                        {'id': 5}
                    ]
                },
                {
                    'wantedShifts': [],
                    'unwantedShifts': [
                        {'id': 1}
                    ]
                },
                {
                    'wantedShifts': [
                        {'id': 3}
                    ],
                    'unwantedShifts': []
                },
                ...
            ]

        keys:
            A pair of str. Each str must represent a key of both 
            dayConfs and shiftConfs as described above.

            Example: ('wantedShifts', 'unwantedShifts')

        daysOnMonth:
            A list of datetime.date objects. Each date should be a day
            of the month whose shifts are being scheduled. The list 
            should contain one and exactly one date object for each day
            of the month.

            This list can be easily genetared as:
                daysOfMonth = [day 
                        for day in calendar.itermonthdates(year, month) 
                        if day.month == month]

            Where 'calendar' is an instance of calendar.Calendar, and
            year and month are ints. 
            
            Note the call above will return the days of the given month 
            in ascending order. This is, the first element will be a 
            date object represeting the first day of the month, the 
            second element will represent the second day of the month,
            and so on.

    Returns:
        A dictionary that will have an entry for each day of the month

        Each key will be a day of the month. E.g. 1, 2, 3, ...

        Each value will be a list of exactly two elements:
          - The first element will be a list of doctorIds, representing 
            the doctors who would have a keys[0] shift this day
          - The second element will be another list of doctorIds, 
            representing the doctors who would have a keys[1] shift 
            this day

        Example:
            {
                1: [[], [1, 3]],
                2: [[], []],
                3: [[], [5]],
                4: [[3], []],
                ...
             }

             If we supose keys was ('wantedShifts', 'unwantedShifts'),
             then this object means as follows:
                The doctors with id 1 and 3 would not like to have a 
                shift the first day of the month, the doctor with id 5
                would not like to have a shift the third day of the 
                month, and the doctor with id 3 would like to have a 
                shift the fourth day of the month
    '''
    log = logging.getLogger('scheduler.getShiftPreferences')
    log.info('Requested the shift preferences: {}'.format(keys))

    key1 =  keys[0]
    key2 = keys[1]

    log.debug('Getting shift preferences by week day')
    '''These two lists will contain an entry for each day of the week. 

    The first element of shifts1ByWeekDay is a list doctorIds 
    representing the doctors who would have their key1 shifts on 
    Mondays, the second element represents doctors who would have their 
    key1 shifts of Tuesdays, and so on. 

    (Idem for the corresponding key2 list)
    '''
    shifts1ByWeekDay = [[] for i in range(7)]
    shifts2ByWeekDay = [[] for i in range(7)]
    for shiftConf in shiftConfs:
        docId = shiftConf['doctorId']
        for shift1ByWeekDay in shiftConf.get(key1, []):
            weekday = WEEK_DAY[shift1ByWeekDay['shift']]
            shifts1ByWeekDay[weekday].append(docId)
        for shift2ByWeekDay in shiftConf.get(key2, []):
            weekday = WEEK_DAY[shift2ByWeekDay['shift']]
            shifts2ByWeekDay[weekday].append(docId)
    log.debug('{} by week day: {}'.format(key1, shifts1ByWeekDay))
    log.debug('{} by week day: {}'.format(key2, shifts2ByWeekDay))
    for i in range(7):
        intersection = set(shifts1ByWeekDay[i]) & set(shifts2ByWeekDay[i])
        if len(intersection) != 0:
            log.warn(('The doctors with id {} have selected the shifts on {} '
                + 'as both a {} and {}').format(intersection, 
                DAY_NUM_TO_WEEK_DAY[i], key1, key2))

    shiftPreferences = {}
    for day, dayConf in zip(daysOfMonth, dayConfs):
        log.debug('Getting shift preferences information for day {}'
            .format(day))

        weekday = day.weekday()
        log.debug('This day is {}'.format(DAY_NUM_TO_WEEK_DAY[weekday]))

        log.debug('Getting high priority shift preferences')
        shifts1HighPriority = [doctor['id'] for doctor in dayConf.get(key1, [])]
        shifts2HighPriority = [doctor['id'] for doctor in dayConf.get(key2, [])]
        log.debug('High priority {} are: {}'.format(key1, 
            shifts1HighPriority))
        log.debug('High priority{} are: {}'.format(key2, 
            shifts2HighPriority))

        intersection = set(shifts1HighPriority) & set(shifts2HighPriority)
        if (len(intersection) != 0):
            log.warn(('The doctors with id {intersection} have selected '
                + 'shift of the day {} as both a {} and {}')
                .format(intersection, day, key1, key2))

        log.debug('Filtering week day preferences according to high '
            + 'priority ones')
        shifts1Filtered = [docId for docId in shifts1ByWeekDay[weekday] 
            if docId not in shifts2HighPriority]
        shifts2Filtered = [docId for docId in shifts2ByWeekDay[weekday]
            if docId not in shifts1HighPriority]
        log.debug('{} after filtering is: {}'.format(key1, shifts1Filtered))
        log.debug('{} after filtering is: {}'.format(key2, shifts2Filtered))

        log.debug('Combining filtered shift preferences with high priority '
            + 'ones')
        shifts1Combined = list(set(shifts1Filtered) | set(shifts1HighPriority))
        shifts2Combined = list(set(shifts2Filtered) | set(shifts2HighPriority))
        log.debug('{} after combining is: {}'.format(key1, shifts1Combined))
        log.debug('{} after combining is: {}'.format(key2, shifts2Combined))

        shiftPreferences[day.day] = [
            shifts1Combined,
            shifts2Combined
        ]

    log.info('The shift preferences are: {}'.format(shiftPreferences))

    return shiftPreferences

def getConfiguration(confDict, key, default=None):
    '''Extract a configuration parameter from the configuration dict

    Params:
        confDict: a dict with the following structure:
            {
                'key1': {
                    'value': 'myvalue1'
                },
                'key2': {
                    'value': 'myvalue2'
                },
                ...
            }
        key: an str. One of the keys of the confDict. In the example 
            above, 'key1' or 'key2' would be valid values
        default: the value to return if the key is not found. In case 
            the key is not found, an ERROR message will be logged.
            Defaults to None

        Returns:
            The value corresponding to the given key, or the provided 
            default
    '''
    log = logging.getLogger('scheduler.schedule')
    value = confDict.get(key, {}).get('value', None)
    if value is None:
        log.error(f'The {key} is not correctly configured. '
            + f'Using the default {default}')
        value = default
    return value

def schedule(doctors, shiftConfs, calendarDict, schedulerConf):
    '''Returns the schedule shifts using the given information

    Args:
        doctors:
            List of dicts. Each dict represents a doctor.

            The doctor dict has to contain the keys:
                id: An int represeting the id of a doctor. There must 
                    not be two doctors with the same id.

                absence: A dict with two keys 'start' and 'end', each
                    having as a value an str represeting a date in ISO 
                    format. The absence can be None.

            NOTE: All doctors in this list will be assigned 
            cycle-shifts and non-cycle-shifts (according to their 
            preferences). If a doctor is DELETED, it should not be 
            included in this list

        shiftConfs:
            List of dicts. Each dict represents the shift configuration
            of a doctor.

            Each shiftConf dict has to contains the keys:
                doctorId: An int representing the id of a doctor. There
                    must not be two shift configurations for the same
                    doctor.

                numConsultations: An int representing whether this 
                    doctor does consultation shifts (> 0) or not (== 0)

                doesCycleShifts: A bool representing whether this 
                    doctor does cycle-shifts or not

                hasShiftsOnlyWhenCycleShifts: A bool representing 
                    wherther this doctor should only have 
                    non-cycle-shifts whenever they have a cycle-shift.
                    This property has a higher preference than 
                    minShifts and maxShifts, meaning these last two 
                    will be ignored if this property is true.

                maxShifts: An int representing the maximum number of
                    shifts this doctor can have.
                    E.g. 4 -> The doctor must have 4 shifts or less

                minShifts: An int representing the minimum number of 
                    shifts this doctor can have. 
                    E.g. 2 -> The doctor must have 2 or more shifts

                unwantedShifts:
                unavailableShifts:
                wantedShifts:
                mandatoryShifts:
                    These four keys together are refered to as 'shift
                    preferences'. Their values should be lists dicts.
                    Each dict must have a key 'shift' having as a value
                    an str. This str must be a day of a week
                    E.g. mandatoryShifts: [
                            {'shift': 'Thursday'},
                            {'shift': 'Tuesday'}
                         ]
                wantedConsultations:
                    Idem as shift preferences, but refer to 
                    consultations.

        calendarDict:
            A dict with the following structure:
                {
                    'month': 6,
                    'year': 2020,
                    'dayConfigurations': [
                        {
                            'day': 1,
                            'isWorkingDay': true,
                            'numShifts': 2,
                            'numConsultations': 0,
                            'unwantedShifts': [
                                {'id': idDoctor1}
                                {'id': idDoctor2}, 
                                ...
                            ],
                            'unavailableShifts': [],
                            'wantedShifts': [
                                {'id': idDoctor3}
                                {'id': idDoctor4}, 
                                ...
                            ],
                            'mandatoryShifts': [],
                            'cycleChanges': []
                        },
                        {
                            'day': 2,
                            'isWorkingDay': true,
                            'numShifts':2,
                            'numConsultations': 0,
                            'unwantedShifts': [],
                            'unavailableShifts': [],
                            'wantedShifts': [
                                {'id': idDoctor1}
                                {'id': idDoctor2}, 
                                ...
                            ],
                            'mandatoryShifts': [
                                {'id': idDoctor7}
                                {'id': idDoctor8}, 
                                ...
                            ],
                            'cycleChanges': [
                                'cyleGiver': {'id': idDoctor1},
                                'cycleReceiver': {'id': idDoctor2}
                            ]
                        },
                        ...
                    ]
                }

        schedulerConf:
            See the documentation of the getConfiguration function

    Returns:
        A dict with the following structure (If there has been an error 
        during the generation, STATUS will be GENERATION_ERROR, and 
        days will be an empty list. Otherwise, STATUS will be 
        PENDING_CONFIRMATION, and days will be a list of all days in 
        the month):
        {
            'month': month,
            'year': year,
            'status': 'STATUS',
            'days': [
                {
                    'day': 1, 
                    'isWorkingDay': True,
                    'cycle': [
                        {'id': idDoctor1}
                        {'id': idDoctor2}, 
                        ...
                    ], 
                    'shifts': [
                        {'id': idDoctor1}
                        {'id': idDoctor4}, 
                        ...
                    ],  
                    'consultations':[]
                },
                {
                    'day': 2, 
                    'isWorkingDay': True,
                    'cycle': [
                        {'id': idDoctor3}
                        {'id': idDoctor4}, 
                        ...
                    ], 
                    'shifts': [
                        {'id': idDoctor4}
                        {'id': idDoctor5}, 
                        ...
                    ],  
                    'consultations':[
                        {'id': idDoctor1} 
                        ...
                    ], 
                },
                ...
            ]
        }
    '''
    log = logging.getLogger('scheduler.schedule')
    log.debug(('Request to schedule with doctors: {}, shiftConfs: {} '
        + 'and calendarDict: {}').format(doctors, shiftConfs, calendarDict))

    year = calendarDict['year']
    month = calendarDict['month']
    log.info('Generating schedule for {}-{}'.format(year, month))

    # Extract the configuration
    cycleShiftRate = getConfiguration(schedulerConf, 'cycleShiftRate',
        default=DEFAULT_CYCLE_SHIFT_RATE)
    wantedShiftWeight = getConfiguration(schedulerConf, 'wantedShiftWeight',
        default=DEFAULT_WANTED_SHIFT_WEIGHT)
    unwantedShiftWeight = getConfiguration(schedulerConf, 'unwantedShiftWeight',
        default=DEFAULT_UNWANTED_SHIFT_WEIGHT)
    wantedConsultationWeight = getConfiguration(schedulerConf, 
        'wantedConsultationWeight',default=DEFAULT_WANTED_CONSULTATION_WEIGHT)
    allShiftWeight = getConfiguration(schedulerConf, 'allShiftWeight',
        default=DEFAULT_ALL_SHIFT_WEIGHT)
    consultationWeight = getConfiguration(schedulerConf, 'consultationWeight',
        default=DEFAULT_CONSULTATION_WEIGHT)
    log.debug(('The values extracted from the configuration are: '
        + 'cycleShiftRate={}, wantedShiftWeight={}, unwantedShiftWeight={}, '
        + 'wantedConsultationWeight={}, allShiftWeight={}, '
        + 'consultationWeight={}').format(cycleShiftRate, wantedShiftWeight, 
        unwantedShiftWeight, wantedConsultationWeight, allShiftWeight, 
        consultationWeight))

    # The list of dayConfigurations sorted by day number
    dayConfs = sorted(calendarDict['dayConfigurations'], 
                        key=lambda day: day['day'])
    log.debug('dayConfs after being sorted is {}'.format(dayConfs))

    # The calendar object will be used to iterate over the days of a month
    calendar = calendarLib.Calendar()

    # The function itermonthdates will not only return the dates in the 
    # specified month, but also all days before the start of the month or 
    # after the end of the month that are required to get a complete week.
    # Each elemnt of daysOfMonth will be a datetime.date object
    daysOfMonth = [day for day in calendar.itermonthdates(year, month) 
                    if day.month == month]
    numDaysInMonth = len(daysOfMonth)
    log.debug('The days in this month are {}'.format(daysOfMonth))

    # Check all needed days are present
    if numDaysInMonth != len(dayConfs):
        errorMessage = ('The number of expected days for {}-{} is {}, but the '
            + 'number of days given was {}').format(year, month, 
            numDaysInMonth, len(dayConfs))
        log.error(errorMessage)
        log.error('Raising ValueError')
        raise ValueError(errorMessage)
    for day, dayConf in zip(daysOfMonth, dayConfs):
        if day.day != dayConf['day']:
            errorMessage = ('Missing the day {} in the day configurations of '
                + 'the calendar').format(day.day)
            log.error(errorMessage)
            log.error('Raising ValueError')
            raise ValueError(errorMessage)

    # TODO check that, with the given information, a schedule can be 
    # generated. This is, for example, that the sum of the maxShifts of
    # all doctors is greater than the sum of the number of shifts there
    # has to be each day

    # Extract shift preferences
    requests = getShiftPreferences(shiftConfs=shiftConfs, dayConfs=dayConfs, 
            keys=('wantedShifts', 'unwantedShifts'), daysOfMonth=daysOfMonth)
    log.debug('Requested shifts are: {}'.format(requests))
    required = getShiftPreferences(shiftConfs=shiftConfs, dayConfs=dayConfs, 
            keys=('mandatoryShifts', 'unavailableShifts'), 
            daysOfMonth=daysOfMonth)
    log.debug('Required shifts are: {}'.format(required))
    requestConsultations = getShiftPreferences(shiftConfs=shiftConfs, 
            dayConfs=dayConfs, 
            keys=('wantedConsultations', 'unwantedConsultations'), 
            daysOfMonth=daysOfMonth)
    log.debug('Requested consultations are: {}'.format(requestConsultations))

    # Generate a dict to relate a doctor's id with their shiftConf
    shiftConfsDict = {shiftConf['doctorId']: shiftConf 
        for shiftConf in shiftConfs}
    log.debug('The shift configuration dict is: {}'.format(shiftConfs))

    # First, generate the cycle shifts
    # TODO Cycle changes need to be taken into account
    cycleShifts = {day.day: [] for day in daysOfMonth}
    firstDayOfMonth = daysOfMonth[0]
    for doctor in doctors:
        docId = doctor['id']
        shiftConf = shiftConfsDict.get(docId, None)
        doesCycleShifts = False
        if shiftConf is None:
            log.warn(f'The doctor {docId} does not have a shift configuration'
                + ' assuming the doctor does cycle shifts')
            doesCycleShifts = True 
        elif shiftConf['doesCycleShifts']:
            doesCycleShifts = True
        if doesCycleShifts:
            startDate = datetime.date.fromisoformat(doctor['startDate'])
            log.debug('The start date of the doctor {} is {}'
                .format(docId, startDate))
            difference = (firstDayOfMonth - startDate).days % cycleShiftRate
            log.debug('The difference is: {}'.format(difference))
            if difference == 0:
                firstCycleShift = firstDayOfMonth
                day = 0
            else:
                firstCycleShift = firstDayOfMonth \
                    + datetime.timedelta(days=cycleShiftRate-difference)
            log.debug('The first cycle shift of doctor {} is {}'
                .format(docId, firstCycleShift))
            # Index used to generate the cycle-shifts of this doctor
            i = firstCycleShift.day
            while i <= numDaysInMonth:
                cycleShifts[i].append(docId)
                i += cycleShiftRate
    log.debug('The cycle shifts are: {}'.format(cycleShifts))

    # TODO add doctors with Absences as unavailable

    workingDays = [dayConf['day'] for dayConf in dayConfs if dayConf['isWorkingDay']]
    log.debug('The working days of the month are: {}'.format(workingDays))

    model = cp_model.CpModel()

    log.debug('Starting the generation of the boolean variables')
    '''shiftVars is a dictionary that will contain the BoolVars used in the 
    model.

    The keys of the dictionary will be tuples as (doctorId, dayNumber)

    The values of the dictionary will be lists of size 1 or 2:
      - The first element of the list will always be a BoolVar that 
        represents whether the doctor with id doctorId has a shift the day
        dayNumber
      - The second element is optinal. It will only be present if the doctor
        does consultations. In that case, the element will be another BoolVar
        that will represent whether the doctor has consultations this 
        daynumber
    '''
    shiftVars = {}
    for shiftConf in shiftConfs:
        for dayConf in dayConfs:
            log.debug(('Generating the boolean variables for shiftConfig: '
                + '{} and dayConfig: {}').format(shiftConf, dayConf))
            if not dayConf['isWorkingDay']:
                log.debug('The day is not a working day. Skipping it')
            else:
                log.debug('The day is a working day')
                docId = shiftConf['doctorId']
                dayNum = dayConf['day']
                doctorVars = []
                doctorVars.append(
                    model.NewBoolVar(f'shift_doc{docId}_day{dayNum}_{SHIFT}')
                )
                if shiftConf['numConsultations'] > 0:
                    doctorVars.append(
                        model.NewBoolVar(f'shift_doc{docId}_day{dayNum}_{CONSULT}')
                    )
                shiftVars[docId, dayNum] = doctorVars
    log.debug('The shiftVars are: {}'.format(shiftVars))

    for shiftVar in shiftVars.values():
        log.debug(('A doctor cannot have a shift and a consultation the same '
            + 'day. Adding the restriction sum({}) <= 1').format(shiftVar))
        model.Add(sum(shiftVar) <= 1)

    # If a doctor has a cycle-shift, they have to have a shift that day
    log.debug('Starting the generation of the cycle-shift restrictions')
    for dayNum in workingDays:
        for docId in cycleShifts[dayNum]:
            log.debug('Analyzing the cycle-shifts of doctor {}'.format(docId))
            shiftConf = shiftConfsDict.get(docId, None)
            doesNonCycleShifts = True
            if shiftConf is None:
                log.warn(('The doctor {} does not have a shift configuration. '
                    + 'Assuming they do NOT have non-cycle-shifts')
                    .format(docId))
                doesNonCycleShifts = False
            elif shiftConf['maxShifts'] == 0 \
                and not shiftConf['hasShiftsOnlyWhenCycleShifts']:
                log.debug('The doctor {} does not have non-cycle-shifts'
                    .format(docId))
                doesNonCycleShifts = False
            if doesNonCycleShifts:
                log.debug(('The doctor {} has a cycle shift on day {} adding '
                    + 'the restriction {} == 1').format(docId, dayNum, 
                    shiftVars[docId, dayNum][0]))
                model.Add(shiftVars[docId, dayNum][0] == 1)

    # Each doctor has a maximum and a minimum number of shifts
    log.debug('Starting the generation of maximum and minimum number of '
        + 'shifts per doctor')
    for docId, shiftConf in shiftConfsDict.items():
        if shiftConf['hasShiftsOnlyWhenCycleShifts']:
            log.debug(('Doctor {} only has shifts when cycle-shifts, so no '
                + 'need to add its maximum and minimum restrictions')
            .format(docId))
        else:
            log.debug(('Creating maximum and minimum shift restrictions for '
                + 'doctor {} with shiftConf {}').format(docId, shiftConf))

            doctorShiftVars = [shiftVars[docId, dayNum][0] 
                for dayNum in workingDays]
            log.debug(('Minimum number of shifts: sum({}) >= {}')
                .format(doctorShiftVars, shiftConf['minShifts']))
            model.Add(sum(doctorShiftVars) >= shiftConf['minShifts'])

            # The maximum number of shifts also includes consultations
            allDoctorVars = [var for dayNum in workingDays 
                for var in shiftVars[docId, dayNum]]
            log.debug(('Maximum number of shifts: sum({}) <= {}')
                .format(allDoctorVars, shiftConf['maxShifts']))
            model.Add(sum(allDoctorVars) <= shiftConf['maxShifts'])

            # If the doctor has consultations, restrict the number of 
            # consultations it can have
            if shiftConf['numConsultations'] > 0:
                doctorConsultationVars = [shiftVars[docId, dayNum][1] 
                    for dayNum in workingDays]
                log.debug(('Minimum number of consultations: sum({}) >= {}')
                    .format(doctorConsultationVars, 
                        shiftConf['numConsultations']))
                model.Add(sum(doctorConsultationVars) 
                    <= shiftConf['numConsultations'])

    # Each day there is a minimum number of shifts and consultations
    log.debug('Starting the generation of maximum and minimum number of '
        + 'shifts per day')
    for dayNum in workingDays:
        dayShiftVars = [shiftVars[docId, dayNum][0] for docId in shiftConfsDict]
        log.debug('Minimum number of shifts on day {}: sum({}) >= {}'
            .format(dayNum, dayShiftVars, dayConfs[dayNum-1]['numShifts']))
        model.Add(sum(dayShiftVars) >= dayConfs[dayNum-1]['numShifts'])

        dayConsultationsVar = [shiftVars[docId, dayNum][1] 
            for docId in shiftConfsDict 
            if len(shiftVars[docId, dayNum]) > 1]
        log.debug('Minimum number of consultations on day {}: sum({}) >= {}'
            .format(dayNum, dayConsultationsVar, 
                dayConfs[dayNum-1]['numConsultations']))
        model.Add(sum(dayConsultationsVar) >= dayConfs[dayNum-1]['numConsultations'])

    log.debug('Starting the construction of the objective function')
    # Wanted shifts contribute positively to the objective function
    wantedShiftContribution = \
        sum(wantedShiftWeight * shiftVars[docId, dayNum][0] 
            for dayNum in workingDays 
            for docId in requests[dayNum][0])
    # Unwanted shifts contribute negatively to the objective function
    unwantedShiftContribution = \
        - sum(unwantedShiftWeight * shiftVars[docId, dayNum][0] 
            for dayNum in workingDays 
            for docId in requests[dayNum][1])
    # Wanted consultations contribute positively
    wantedConsultationContribution = \
        sum(wantedConsultationWeight * shiftVars[docId, dayNum][1]
            for dayNum in workingDays 
            for docId in requestConsultations[dayNum][0])
    # All shifts contribute negatively (to minimize the number of 
    # shifts scheduled)
    allShiftContribution = - sum(allShiftWeight * shiftVar[0] 
                                for shiftVar in shiftVars.values())
    # Consultations contribute positively (to give preference to a 
    # consultation over a regular shift)
    consultationContribution = sum(consultationWeight * shiftVar[1] 
                                    for shiftVar in shiftVars.values() 
                                        if len(shiftVar) > 1)

    objectiveFunction = wantedShiftContribution + unwantedShiftContribution \
        + wantedConsultationContribution + allShiftContribution \
        + consultationContribution

    model.Maximize(objectiveFunction)


    # Solve the problem
    solver = cp_model.CpSolver()
    solver.parameters.num_search_workers = 8
    log.info('Starting the solver')
    status = solver.Solve(model)
    optimalOrFeasibleSolutionFound = status == cp_model.OPTIMAL \
        or status == cp_model.FEASIBLE
    if optimalOrFeasibleSolutionFound:
        log.info('The solution found is optimal or feasible')
    else:
        log.error('No solution found')

    # This is the schedule that is going to be returned
    schedule = {
        'month': month,
        'year': year,
        'status': 'PENDING_CONFIRMATION',
        'days': [{
            'day': day.day, 
            'isWorkingDay': False,
            'cycle': [], 
            'shifts': [], 
            'consultations':[]
        } for day in daysOfMonth]
    }
    log.debug('The empty schedule to be filled is: {}'.format(schedule))

    # If the optimal solution was found
    if optimalOrFeasibleSolutionFound:
        log.debug('Assigned shifts are: ')
        for (docId, dayNum), shiftVar in shiftVars.items():
            if solver.BooleanValue(shiftVar[0]):
                log.debug('Doctor {} has a shift on day {}'
                    .format(docId, dayNum))
                schedule['days'][dayNum-1]['shifts'].append({'id':docId})
            if len(shiftVar) > 1 and solver.BooleanValue(shiftVar[1]):
                log.debug('Doctor {} has a consultation on day {}'
                    .format(docId, dayNum))
                schedule['days'][dayNum-1]['consultations'].append({'id':docId})
        for day in daysOfMonth:
            schedule['days'][day.day-1]['cycle'] = \
                [{'id': docId} for docId in cycleShifts[day.day]]
            if dayConfs[day.day-1]['isWorkingDay']:
                schedule['days'][day.day-1]['isWorkingDay'] = True
    else:
        schedule['status'] = 'GENERATION_ERROR'
        schedule['days'] = []
    log.debug(solver.ResponseStats())

    log.debug('The generated schedule is: {}'.format(schedule))

    return schedule
